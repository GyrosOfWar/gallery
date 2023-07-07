import type {Dispatch, SetStateAction} from "react"
import {Upload} from "tus-js-client"

export function uploadFile<F extends object>(
  file: File,
  info: F,
  accessToken: string,
  endpoint: string,
  setProgress?: Dispatch<SetStateAction<Progress>>,
): Promise<void> {
  return new Promise((resolve, reject) => {
    if (setProgress) {
      setProgress((oldProgress) => ({
        ...oldProgress,
        currentFileTotal: file.size,
      }))
    }

    const upload = new Upload(file, {
      endpoint,
      metadata: {
        filename: file.name,
        filetype: file.type,
        ...info,
      },
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
      onProgress(bytesUploaded, bytesTotal) {
        if (setProgress) {
          setProgress((oldProgress) => ({
            ...oldProgress,
            currentFileCompleted: bytesUploaded,
            currentFileTotal: bytesTotal,
          }))
        }
      },
      onBeforeRequest(req) {
        const xhr = req.getUnderlyingObject() as XMLHttpRequest
        xhr.withCredentials = true
      },
      onSuccess() {
        if (setProgress) {
          setProgress((oldProgress) => ({
            filesCompleted: oldProgress.filesCompleted + 1,
            filesTotal: oldProgress.filesTotal,
            currentFileCompleted: 0,
            currentFileTotal: 0,
          }))
        }
        resolve()
      },
      onError(error) {
        reject(error)
      },
    })
    upload.start()
    // TODO
    // upload.findPreviousUploads().then((uploads) => {
    //   if (uploads.length) {
    //     upload.resumeFromPreviousUpload(uploads[0])
    //   }
    //   upload.start()
    // })
  })
}

export interface Progress {
  filesTotal: number
  filesCompleted: number
  currentFileTotal: number
  currentFileCompleted: number
}
