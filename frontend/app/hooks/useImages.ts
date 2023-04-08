import {useFetcher} from "@remix-run/react"
import type React from "react"
import {useEffect, useState} from "react"
import useInfiniteScroll from "react-infinite-scroll-hook"
import type {ClientImageList, ClientImagePage, Data} from "~/routes/api/image"

export interface UseImagesInput {
  initialPage: ClientImagePage
}

export interface UseImagesResult {
  images: ClientImageList
  sentryRef: React.Ref<HTMLDivElement>
  hasNextPage: boolean
  loading: boolean
  setPages: React.Dispatch<React.SetStateAction<ClientImagePage[]>>
  lastPage: ClientImagePage
}

export default function useImages({
  initialPage,
}: UseImagesInput): UseImagesResult {
  const fetcher = useFetcher<Data>()
  const [pages, setPages] = useState([initialPage])
  const page = pages[pages.length - 1]
  const loading = fetcher.state !== "idle"
  const total = page?.totalPages || 0
  const number = page?.pageNumber || 0
  const hasNextPage = number < total - 1
  const images = pages.flatMap((p) => p.content).filter(Boolean)

  const loadMore = () => {
    const nextPage = (page.pageNumber || 0) + 1
    fetcher.load(`/?index&page=${nextPage}`)
  }

  const [sentryRef] = useInfiniteScroll({
    loading,
    hasNextPage,
    onLoadMore: loadMore,
  })

  useEffect(() => {
    if (fetcher.data) {
      setPages((oldPages) => {
        if (fetcher.data && !fetcher.data.images.empty) {
          return [...oldPages, fetcher.data.images]
        } else {
          return oldPages
        }
      })
    }
  }, [fetcher.data])

  return {
    images,
    loading,
    hasNextPage,
    sentryRef,
    setPages,
    lastPage: page,
  }
}
