import {MapContainer, Marker, Popup, TileLayer} from "react-leaflet"

const OpenStreetMapEmbed: React.FC<{
  lat: number
  lon: number
  name: string | null | undefined
}> = ({lat, lon, name}) => {
  return (
    <MapContainer
      className="h-64"
      center={[lat, lon]}
      zoom={13}
      scrollWheelZoom={false}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Marker position={[lat, lon]}>
        <Popup>{name}</Popup>
      </Marker>
    </MapContainer>
  )
}

export default OpenStreetMapEmbed
