import {useOutletContext} from "@remix-run/react"
import FileDrop from "~/components/FileDrop"
import type {OutletData} from "~/root"
import {uploadFile} from "~/services/upload.client"
import {backendUrl} from "~/util/consts"

const ImportPage: React.FC = () => {
  const {user} = useOutletContext<OutletData>()

  const onDrop = async (files: File[]) => {
    await uploadFile(
      files[0],
      {},
      user!.accessToken,
      backendUrl("/api/batch-import")
    )
  }

  return (
    <div>
      <FileDrop onDrop={onDrop} />
    </div>
  )
}

export default ImportPage
