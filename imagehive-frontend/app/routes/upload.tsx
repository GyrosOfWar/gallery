import {ArrowUpTrayIcon} from "@heroicons/react/24/outline"
import {Form} from "@remix-run/react"
import clsx from "clsx"
import {Button, FileInput, Label, TextInput} from "flowbite-react"
import {useCallback, useEffect, useState} from "react"
import {useDropzone} from "react-dropzone"

interface FileWithUrl {
  file: File
  url: string
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

  const {getRootProps, getInputProps, isDragActive} = useDropzone({
    onDrop,
    multiple: true,
  })

  useEffect(() => {
    return () => files?.forEach((file) => URL.revokeObjectURL(file.url))
  }, [files])

  return (
    <>
      <h1 className="text-3xl font-bold mb-4">Upload</h1>
      {!previewImages && (
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
      )}

      {previewImages && (
        <div className="flex">
          <section className="grid grid-cols-4 gap-2">
            {files?.map((file) => (
              <img src={file.url} key={file.file.name} alt={file.file.name} />
            ))}
          </section>
          <section className="min-w-fit">
            <Form className="px-4">
              <div>
                <Label htmlFor="title-input">Title</Label>
                <TextInput
                  id="title-input"
                  placeholder=""
                  name="title"
                  type="text"
                />
              </div>
            </Form>
          </section>
        </div>
      )}
    </>
  )
}

export default UploadPage
