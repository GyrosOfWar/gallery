export interface Props extends React.InputHTMLAttributes<HTMLInputElement> {
  editMode: boolean
  value?: string
  defaultValue?: string
  name: string
}

const ToggleableInput: React.FC<Props> = ({
  editMode,
  value,
  defaultValue,
  name,
  ...rest
}) => {
  return (
    <>
      {editMode && (
        <input
          name={name}
          type="text"
          className="block p-0 border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600"
          value={value}
          defaultValue={defaultValue}
          {...rest}
        />
      )}
      {!editMode && (
        <p className=" border-b-2 border-b-transparent">
          {value || defaultValue}
        </p>
      )}
    </>
  )
}

export default ToggleableInput
