import clsx from "clsx"

interface Props {
  children?: React.ReactNode
  className?: string
}

const Card: React.FC<Props> = ({className, children}) => {
  return (
    <article
      className={clsx(
        "flex flex-col justify-between bg-white dark:bg-gray-800 rounded-xl p-2 shadow-lg",
        className,
      )}
    >
      {children}
    </article>
  )
}

export default Card
