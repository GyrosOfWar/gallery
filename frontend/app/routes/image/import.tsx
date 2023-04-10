import {useNavigate, useOutletContext} from "@remix-run/react"
import {Spinner} from "flowbite-react"
import {useState} from "react"
import FileDrop from "~/components/FileDrop"
import type {OutletData} from "~/root"
import {uploadFile} from "~/services/upload.client"
import {backendUrl} from "~/util/consts"

const ImportPage: React.FC = () => {
  const {user} = useOutletContext<OutletData>()
  const navigate = useNavigate()
  const [uploading, setUploading] = useState(false)

  const onDrop = async (files: File[]) => {
    setUploading(true)
    await uploadFile(
      files[0],
      {},
      user!.accessToken,
      backendUrl("/api/batch-import")
    )
    navigate("/")
  }

  return (
    <div className="flex items-center flex-col">
      <h1 className="text-3xl font-bold self-start mb-4">
        Import Google Takoeut archives
      </h1>
      {!uploading && (
        <FileDrop onDrop={onDrop} accept={{"application/zip": [".zip"]}} />
      )}
      {uploading && (
        <p className="p-16 text-center text-xl flex items-center justify-center">
          <Spinner className="mr-4" /> Uploading archive...
        </p>
      )}
    </div>
  )
}

export default ImportPage
