interface Props {
  children: React.ReactNode
}

const KVList = ({children}: Props) => {
  return (
    <dl className="max-w-md my-4 text-gray-900 divide-y divide-gray-200 dark:text-white dark:divide-gray-700">
      {children}
    </dl>
  )
}

const Item: React.FC<{children: React.ReactNode}> = ({children}) => {
  return <div className="flex flex-col pb-3">{children}</div>
}

const Key: React.FC<{children: React.ReactNode}> = ({children}) => {
  return (
    <dt className="mb-1 text-gray-500 md:text-lg dark:text-gray-400">
      {children}
    </dt>
  )
}

const Value: React.FC<{children: React.ReactNode}> = ({children}) => {
  return <dd className="text-lg font-semibold">{children}</dd>
}

KVList.Item = Item
KVList.Key = Key
KVList.Value = Value

export default KVList
