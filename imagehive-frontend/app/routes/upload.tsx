import {ArrowUpTrayIcon} from "@heroicons/react/24/outline"
import type {ActionFunction} from "@remix-run/node"
import {Form} from "@remix-run/react"
import clsx from "clsx"
import {Button, TextInput} from "flowbite-react"
import {useCallback, useState} from "react"
import type {DropzoneOptions} from "react-dropzone"
import {useDropzone} from "react-dropzone"

interface FileWithUrl {
  file: File
  url: string
}

export const action: ActionFunction = async ({context, request}) => {
  const data = await request.formData()
  console.log(Array.from(data.entries()))
  return 1
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
  const size = files?.reduce((total, {file}) => total + file.size, 0)

  return (
    <Form method="post">
      <div className="fixed left-0 bottom-0 w-full py-2 z-10 border-t bg-white border-t-black border-opacity-50 grid grid-cols-3 items-center">
        <div></div>
        <div className="text-center">
          <strong>{files?.length}</strong> files selected for upload (total
          size: {size})
        </div>
        <Button
          className="place-self-end"
          color="success"
          size="lg"
          type="submit"
        >
          <ArrowUpTrayIcon className="w-4 h-4 mr-2" />
          Upload
        </Button>
      </div>

      <section className="grid grid-cols-4 gap-2 pb-16">
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

  // useEffect(() => {
  //   return () => files?.forEach((file) => URL.revokeObjectURL(file.url))
  // }, [files])

  return (
    <>
      <h1 className="text-3xl font-bold mb-4">Upload</h1>
      {!previewImages && <UploadStep onDrop={onDrop} />}
      {previewImages && <PreviewStep files={files} />}
    </>
  )
}

export default UploadPage
