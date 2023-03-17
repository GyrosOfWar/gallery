import type {RangeSliderProps} from "flowbite-react"
import {RangeSlider} from "flowbite-react"

interface SliderProps extends RangeSliderProps {
  min: number
  max: number
  className?: string
}

function Slider({min, max, className, ...rest}: SliderProps) {
  return (
    <RangeSlider
      title="Control the number of columns"
      min={min}
      max={max}
      className={className}
      {...rest}
    />
  )
}

export default Slider
