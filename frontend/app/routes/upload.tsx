import {HiUpload} from "react-icons/hi"
import type {LoaderFunction} from "@remix-run/node"
import {json} from "@remix-run/node"
import {useLoaderData, useNavigate} from "@remix-run/react"
import {Button, TextInput} from "flowbite-react"
import produce from "immer"
import {useCallback, useState} from "react"
import type {DropzoneOptions} from "react-dropzone"
import Card from "~/components/Card"
import type {User} from "~/services/auth.server"
import {requireUser} from "~/services/auth.server"
import {backendUrl} from "~/util/consts"
import type {Progress} from "~/services/upload.client"
import {uploadFile} from "~/services/upload.client"
import FileDrop from "~/components/FileDrop"

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
  return (
    <section className="self-center mt-8">
      <FileDrop
        onDrop={onDrop}
        multiple
        accept={{"image/*": [".jpg", ".jpeg", ".png"]}}
      />
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
  <div className="grid grid-cols-2 items-center">
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
      <HiUpload className="w-6 h-6 mr-2" />
      Upload
    </Button>
  </div>
)

const ProgressBar: React.FC<{
  completed: number
  total: number
  title: string
}> = ({completed, total, title}) => {
  const progress = Math.round((completed / total) * 100)

  return (
    <div>
      <div className="flex justify-between mb-1">
        <span className="text-base font-medium text-blue-700 dark:text-white">
          {title}
        </span>
        <span className="text-sm font-medium text-blue-700 dark:text-white">
          {progress}%
        </span>
      </div>
      <div className="w-full bg-gray-200 rounded-full h-2.5 dark:bg-gray-700">
        <div
          className="bg-blue-600 h-2.5 rounded-full"
          style={{
            width: `${progress}%`,
          }}
        ></div>
      </div>
    </div>
  )
}

const PreviewStep: React.FC<{files: FileWithUrl[]; user: User}> = ({
  files,
  user,
}) => {
  const size = files.reduce((total, {file}) => total + file.size, 0) || 0
  const formattedSize = (size / MEGABYTES).toFixed(2)
  const [formState, setFormState] = useState<FieldData[]>(
    files.map((f) => ({tags: "", title: f.file.name, description: ""}))
  )
  const [uploading, setUploading] = useState(false)
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

  // eslint-disable-next-line @typescript-eslint/no-misused-promises
  const onSubmit: React.FormEventHandler<HTMLFormElement> = useCallback(
    async (event) => {
      event.preventDefault()
      setUploading(true)
      const endpoint = backendUrl("/api/images/upload")

      const promises = files.map(({file}, index) => {
        const info = formState[index]
        return uploadFile(file, info, user.accessToken, endpoint, setProgress)
      })
      await Promise.all(promises)
      navigate("/")
    },
    [files, formState, user.accessToken, navigate]
  )

  return (
    <form onSubmit={onSubmit}>
      <aside className="fixed left-0 bottom-0 w-full py-2 z-10 border-t bg-white dark:bg-gray-800 border-t-black dark:border-t-gray-300 border-opacity-50">
        <div className="container ml-auto mr-auto px-2">
          {uploading ? (
            <div className="flex flex-col gap-2">
              <ProgressBar
                total={progress.currentFileTotal}
                completed={progress.currentFileCompleted}
                title="Current file"
              />
              <ProgressBar
                total={progress.filesTotal}
                completed={progress.filesCompleted}
                title="All files"
              />
            </div>
          ) : (
            <InfoBar
              count={files.length}
              uploading={uploading}
              formattedSize={formattedSize}
            />
          )}
        </div>
      </aside>

      <section className="grid grid-cols-1 lg:grid-cols-4 gap-2 pb-20">
        {files.map(({file, url}, index) => (
          <Card key={file.name}>
            <img loading="lazy" src={url} alt={file.name} />
            <div className="flex flex-col gap-2 mt-2">
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
          </Card>
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
