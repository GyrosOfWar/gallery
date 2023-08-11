import {useState} from "react"
import {Button, Card, Label, TextInput} from "flowbite-react"

import type {UpdatePasswordDTO} from "imagehive-client"

const SecurityUserSettingsPage = () => {
  const [oldPassword, setOldPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [confirmNewPassword, setConfirmNewPassword] = useState("")

  const onSubmitPasswordChange = async (
    e: React.FormEvent<HTMLFormElement>,
  ) => {
    e.preventDefault()
    const passwordChangeDTO = {
      oldPassword,
      newPassword,
      confirmNewPassword,
    } satisfies UpdatePasswordDTO

    await fetch("/api/user/settings/updatePassword", {
      method: "POST",
      body: JSON.stringify(passwordChangeDTO),
    })
  }

  return (
    <Card>
      <h2>Change password</h2>
      <hr />
      <form className="flex flex-col gap-4" onSubmit={onSubmitPasswordChange}>
        <div>
          <div className="mb-2 block">
            <Label htmlFor="oldPassword" value="Old password" />
          </div>
          <TextInput
            id="oldPassword"
            required
            type="password"
            onChange={(e) => setOldPassword(e.target.value)}
            value={oldPassword}
          />
        </div>
        <div>
          <div className="mb-2 block">
            <Label htmlFor="newPassword" value="New password" />
          </div>
          <TextInput
            id="newPassword"
            required
            type="password"
            onChange={(e) => setNewPassword(e.target.value)}
            value={newPassword}
          />
        </div>
        <div>
          <div className="mb-2 block">
            <Label htmlFor="confirmNewPassword" value="Confirm new password" />
          </div>
          <TextInput
            id="confirmNewPassword"
            required
            type="password"
            onChange={(e) => setConfirmNewPassword(e.target.value)}
            value={confirmNewPassword}
          />
        </div>
        <Button type="submit">Update password</Button>
      </form>
      <hr />
    </Card>
  )
}

export default SecurityUserSettingsPage
