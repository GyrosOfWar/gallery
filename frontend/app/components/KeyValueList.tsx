import {clsx} from "clsx"

interface Props {
  children: React.ReactNode
  className?: string
}

const KVList = ({children, className}: Props) => {
  return (
    <dl
      className={clsx(
        "max-w-md text-center my-4 text-gray-900 divide-y divide-gray-200 dark:text-white dark:divide-gray-700",
        className
      )}
    >
      {children}
    </dl>
  )
}

const Item: React.FC<Props> = ({children, className}) => {
  return <div className={clsx("flex flex-col pb-3", className)}>{children}</div>
}

const Key: React.FC<Props> = ({children, className}) => {
  return (
    <dt
      className={clsx(
        "mb-1 text-gray-500 md:text-lg dark:text-gray-400",
        className
      )}
    >
      {children}
    </dt>
  )
}

const Value: React.FC<Props> = ({children, className}) => {
  return (
    <dd className={clsx("text-lg font-semibold", className)}>{children}</dd>
  )
}

KVList.Item = Item
KVList.Key = Key
KVList.Value = Value

export default KVList
