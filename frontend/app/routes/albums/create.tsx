import {CheckIcon} from "@heroicons/react/24/outline"
import {redirect} from "@remix-run/node"
import {Form} from "@remix-run/react"
import {Button, Label, TextInput} from "flowbite-react"
import type {AlbumDetailsDTO} from "imagehive-client"
import type {ActionFunction} from "react-router"
import {requireUser} from "~/services/auth.server"
import http from "~/util/http"

export const action: ActionFunction = async ({request}) => {
  const formData = await request.formData()
  const {accessToken} = await requireUser(request)
  const payload = Object.fromEntries(formData.entries())

  const response = await http.postJson("/api/albums", payload, accessToken)
  const newAlbum: AlbumDetailsDTO = await response.json()
  return redirect(`/albums/${newAlbum.id}`)
}

const AlbumCreatePage: React.FC = () => {
  return (
    <>
      <h1 className="text-3xl font-bold mb-4">New album</h1>
      <Form className="max-w-xl grow flex flex-col gap-4" method="post">
        <div className="w-full flex flex-col gap-1">
          <Label htmlFor="name-input">Name</Label>
          <TextInput name="name" id="name-input" placeholder="Name" required />
        </div>

        <div className="w-full flex flex-col gap-1">
          <Label htmlFor="description-input">Description</Label>
          <TextInput
            name="description"
            id="description-input"
            placeholder="Description (optional)"
          />
        </div>

        <div className="w-full flex flex-col gap-1">
          <Label htmlFor="tags-input">Tags</Label>
          <TextInput
            name="tags"
            id="tags-input"
            placeholder="Tags (optional)"
          />
        </div>

        <Button type="submit" color="success">
          <CheckIcon className="w-4 h-4 mr-2" />
          Submit
        </Button>
      </Form>
    </>
  )
}

export default AlbumCreatePage
