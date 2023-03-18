import type {ImageDTO} from "imagehive-client"

type Callback = (image: ImageDTO) => void

const useToggleFavorite = (imageId: string, callback?: Callback) => {
  const toggleFavorite = async (event: React.MouseEvent) => {
    event.preventDefault()
    const response = await fetch(`/api/image/${imageId}/favorite`, {
      method: "POST",
    })
    if (!response.ok) {
      // todo better error handling, show a toast or something
      console.error(`request failed with status code ${response.status}`)
    }
    if (callback) {
      const imageDto: ImageDTO = await response.json()
      callback(imageDto)
    }
  }

  return toggleFavorite
}

export default useToggleFavorite
