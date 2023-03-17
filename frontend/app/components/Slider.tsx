import type {ChangeEvent, Dispatch, SetStateAction} from "react"
import {useState} from "react"
import {RangeSlider} from "flowbite-react"

interface SliderProps {
  min: number
  max: number
  onSetImageRange: Dispatch<SetStateAction<number>>
  className?: string
}

function Slider({onSetImageRange, min, max, className}: SliderProps) {
  const [rangeValue, setRangeValue] = useState("4")

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setRangeValue(e.target.value)
    onSetImageRange(parseInt(e.target.value))
  }

  return (
    <RangeSlider
      title="Control the number of columns"
      min={min}
      max={max}
      value={rangeValue}
      onChange={handleChange}
      className={className}
    />
  )
}

export default Slider
