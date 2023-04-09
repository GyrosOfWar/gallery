import clsx from "clsx"
import type {DropzoneOptions} from "react-dropzone"
import {useDropzone} from "react-dropzone"
import {HiUpload} from "react-icons/hi"

export interface Props extends DropzoneOptions {
  className?: string
}

const FileDrop: React.FC<Props> = ({className, ...rest}) => {
  const {getRootProps, getInputProps, isDragActive} = useDropzone(rest)

  return (
    <div
      className={clsx(
        "p-16 text-xl border border-gray-500 border-dashed flex items-center flex-col gap-4 cursor-pointer",
        isDragActive && "bg-green-400 text-white",
        className
      )}
      {...getRootProps()}
    >
      <input {...getInputProps()} />
      <HiUpload className="w-16 h-16" />
      Click here or drag & drop files!
    </div>
  )
}

export default FileDrop
