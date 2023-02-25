import {ArrowUpTrayIcon} from "@heroicons/react/24/outline"
import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {useLoaderData, useNavigate} from "@remix-run/react"
import clsx from "clsx"
import {Button, TextInput} from "flowbite-react"
import produce from "immer"
import {useCallback, useState} from "react"
import type {DropzoneOptions} from "react-dropzone"
import {useDropzone} from "react-dropzone"
import {Upload} from "tus-js-client"
import type {User} from "~/services/auth.server"
import {requireUser} from "~/services/auth.server"
import {backendUrl} from "~/util/consts"

const MEGABYTES = 1000 * 1000

export const loader: LoaderFunction = async ({request}) => {
  const user = await requireUser(request)

  return json({user})
}

interface FileWithUrl {
  file: File
  url: string
}

const UploadStep: React.FC<{onDrop: DropzoneOptions["onDrop"]}> = ({
  onDrop,
}) => {
  const {getRootProps, getInputProps, isDragActive} = useDropzone({
    onDrop,
    multiple: true,
  })

  return (
    <section className="self-center">
      <div
        className={clsx(
          "p-16 text-xl border border-gray-500 border-dashed flex items-center flex-col gap-4 cursor-pointer",
          isDragActive && "bg-green-400 text-white"
        )}
        {...getRootProps()}
      >
        <input {...getInputProps()} />
        <ArrowUpTrayIcon className="w-16 h-16" />
        Drag & drop files here!
      </div>
    </section>
  )
}

interface FieldData {
  tags: string
  title: string
  description: string
}

interface InfoBarProps {
  count: number
  formattedSize: string
  uploading: boolean
}

const InfoBar: React.FC<InfoBarProps> = ({count, formattedSize, uploading}) => (
  <div className="container grid grid-cols-2 items-center ml-auto mr-auto px-2">
    <div>
      <strong>{count}</strong> files selected for upload (total size:{" "}
      {formattedSize} MB)
    </div>
    <Button
      className="place-self-end"
      color="success"
      size="lg"
      type="submit"
      disabled={uploading}
    >
      <ArrowUpTrayIcon className="w-6 h-6 mr-2" />
      Upload
    </Button>
  </div>
)

function uploadFile(
  file: File,
  info: FieldData,
  accessToken: string,
  endpoint: string
): Promise<void> {
  return new Promise((resolve, reject) => {
    const upload = new Upload(file, {
      // send directly to the backend with CORS
      endpoint,
      metadata: {
        filename: file.name,
        filetype: file.type,
        tags: info.tags,
        title: info.title,
        description: info.description,
      },
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
      onProgress(bytesUploaded, bytesTotal) {
        console.log(bytesUploaded, bytesTotal)
      },
      onBeforeRequest(req) {
        const xhr = req.getUnderlyingObject() as XMLHttpRequest
        xhr.withCredentials = true
      },
      onSuccess() {
        resolve()
      },
      onError(error) {
        reject(error)
      },
    })
    upload.start()
    // upload.findPreviousUploads().then((uploads) => {
    //   if (uploads.length) {
    //     upload.resumeFromPreviousUpload(uploads[0])
    //   }
    //   upload.start()
    // })
  })
}

interface Progress {
  filesTotal: number
  filesCompleted: number
  currentFileTotal: number
  currentFileCompleted: number
}

const PreviewStep: React.FC<{files: FileWithUrl[]; user: User}> = ({
  files,
  user,
}) => {
  const size = files.reduce((total, {file}) => total + file.size, 0) || 0
  const formattedSize = (size / MEGABYTES).toFixed(2)
  const [formState, setFormState] = useState<FieldData[]>(
    files!.map(() => ({tags: "", title: "", description: ""}))
  )
  const [uploading, setUploading] = useState(false)
  // TODO show two progress bars
  const [progress, setProgress] = useState({
    filesTotal: files.length,
    filesCompleted: 0,
    currentFileTotal: 0,
    currentFileCompleted: 0,
  } satisfies Progress)
  const navigate = useNavigate()

  const onChange = (index: number, field: keyof FieldData, value: string) => {
    setFormState((state) =>
      produce(state, (draft) => {
        draft[index][field] = value
      })
    )
  }

  const onSubmit: React.FormEventHandler<HTMLFormElement> = useCallback(
    async (event) => {
      event.preventDefault()
      setUploading(true)
      const endpoint = backendUrl("/api/images/upload")

      const promises = files.map(({file}, index) => {
        const info = formState[index]
        return uploadFile(file, info, user.accessToken, endpoint)
      })
      await Promise.all(promises)
      navigate("/")
    },
    [files, formState, user.accessToken, navigate]
  )

  return (
    <form onSubmit={onSubmit}>
      <aside className="fixed left-0 bottom-0 w-full py-2 z-10 border-t bg-white border-t-black border-opacity-50">
        {uploading ? (
          <div className="w-full bg-gray-200 rounded-full dark:bg-gray-700">
            {/* <div
              className="bg-blue-600 text-xs font-medium text-blue-100 text-center p-0.5 leading-none rounded-full"
              style={{width: `${progress}%`}}
            >
              {progress}%
            </div> */}
          </div>
        ) : (
          <InfoBar
            count={files.length}
            uploading={uploading}
            formattedSize={formattedSize}
          />
        )}
      </aside>

      <section className="grid grid-cols-1 lg:grid-cols-4 gap-2 pb-20">
        {files.map(({file, url}, index) => (
          <figure
            key={file.name}
            className="flex flex-col justify-between bg-slate-50 rounded-xl p-2 shadow-lg"
          >
            <img loading="lazy" src={url} alt={file.name} />
            <div className="flex flex-col gap-2">
              <p className="mt-1">
                <strong>File</strong> {file.name}
              </p>
              <TextInput
                name={`${file.name}-title`}
                placeholder="Enter title (optional)"
                onChange={(event) =>
                  onChange(index, "title", event.target.value)
                }
                value={formState[index].title}
                disabled={uploading}
              />
              <TextInput
                name={`${file.name}-description`}
                placeholder="Enter description (optional)"
                onChange={(event) =>
                  onChange(index, "description", event.target.value)
                }
                value={formState[index].description}
                disabled={uploading}
              />
              <TextInput
                name={`${file.name}-tags`}
                placeholder="Enter tags (optional)"
                onChange={(event) =>
                  onChange(index, "tags", event.target.value)
                }
                value={formState[index].tags}
                disabled={uploading}
              />
            </div>
          </figure>
        ))}
      </section>
    </form>
  )
}

const UploadPage: React.FC = () => {
  const [previewImages, setPreviewImages] = useState(false)
  const [files, setFiles] = useState<FileWithUrl[]>()
  const {user} = useLoaderData<{user: User}>()

  const onDrop = useCallback((acceptedFiles: File[]) => {
    const filesWithUrls = acceptedFiles.map((file) => ({
      file: file,
      url: URL.createObjectURL(file),
    }))
    setFiles(filesWithUrls)

    setPreviewImages(true)
  }, [])

  return (
    <>
      <h1 className="text-3xl font-bold mb-4">Upload</h1>
      {!previewImages && <UploadStep onDrop={onDrop} />}
      {previewImages && <PreviewStep user={user} files={files || []} />}
    </>
  )
}

export default UploadPage
