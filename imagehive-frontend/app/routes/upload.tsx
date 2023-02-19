import {ArrowUpTrayIcon} from "@heroicons/react/24/outline"
import type {ActionFunction} from "@remix-run/node"
import {Form, useSubmit} from "@remix-run/react"
import clsx from "clsx"
import {Button, TextInput} from "flowbite-react"
import {useCallback, useEffect, useRef, useState} from "react"
import type {DropzoneOptions} from "react-dropzone"
import {useDropzone} from "react-dropzone"

const MEGABYTES = 1000 * 1000

interface FileWithUrl {
  file: File
  url: string
}

export const action: ActionFunction = async ({context, request}) => {

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

const PreviewStep: React.FC<{files?: FileWithUrl[]}> = ({files}) => {
  const size = files?.reduce((total, {file}) => total + file.size, 0) || 0
  const formattedSize = (size / MEGABYTES).toFixed(2)

  return (
    <Form method="post">
      <aside className="fixed left-0 bottom-0 w-full py-2 z-10 border-t bg-white border-t-black border-opacity-50">
        <div className="container grid grid-cols-2 items-center ml-auto mr-auto px-2">
          <div>
            <strong>{files?.length}</strong> files selected for upload (total
            size: {formattedSize} MB)
          </div>
          <Button
            className="place-self-end"
            color="success"
            size="lg"
            type="submit"
          >
            <ArrowUpTrayIcon className="w-6 h-6 mr-2" />
            Upload
          </Button>
        </div>
      </aside>

      <section className="grid grid-cols-1 lg:grid-cols-4 gap-2 pb-20">
        {files?.map(({file, url}) => (
          <figure key={file.name} className="flex flex-col justify-between">
            <img loading="lazy" src={url} alt={file.name} />
            <div className="flex flex-col gap-2">
              <span>{file.name}</span>
              <TextInput
                name={`${file.name}-title`}
                placeholder="Enter title (optional)"
              />
              <TextInput
                name={`${file.name}-tags`}
                placeholder="Enter tags (optional)"
              />
            </div>
          </figure>
        ))}
      </section>
    </Form>
  )
}

const UploadPage: React.FC = () => {
  const [previewImages, setPreviewImages] = useState(false)
  const [files, setFiles] = useState<FileWithUrl[]>()

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
      {previewImages && <PreviewStep files={files} />}
    </>
  )
}

export default UploadPage
