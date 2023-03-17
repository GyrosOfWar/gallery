import type {ChangeEvent, Dispatch, SetStateAction} from 'react';
import { useState} from 'react'
import {RangeSlider} from "flowbite-react"

interface SliderProps{
    max: number
    onSetImageRange:  Dispatch<SetStateAction<number>>

}

function Slider({onSetImageRange, max}: SliderProps)  {
 const [rangeValue, setRangeValue] = useState("4")

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setRangeValue(e.target.value)
    onSetImageRange(parseInt(e.target.value))
  }

  return(
      <RangeSlider min={1} max={max} value={rangeValue} onChange={handleChange} className="mt-4 mb-8"/>
  )
}

export default Slider