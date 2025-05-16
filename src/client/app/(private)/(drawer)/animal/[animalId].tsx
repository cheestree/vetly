import { useLocalSearchParams, useRouter } from "expo-router";
import AnimalServices from "@/api/services/AnimalServices";
import AnimalDetailsContent from "@/components/animal/AnimalDetailsContent";
import BaseComponent from "@/components/BaseComponent";
import { useAuth } from "@/hooks/useAuth";
import { usePageTitle } from "@/hooks/usePageTitle";
import { useEffect, useState } from "react";

export default function AnimalDetails() {
  const router = useRouter()
  const { animalId } = useLocalSearchParams()
  const { user, loading: authLoading } = useAuth()
  const [animal, setAnimal] = useState<AnimalInformation>()
  const [loading, setLoading] = useState(true)

  usePageTitle(animal ? `Animal ${animal.id}` : 'Animal')

  useEffect(() => {
    const fetchAnimal = async () => {
      try {
        const result = await AnimalServices.getAnimal(animalId?.[0])

        if (!result) {
          router.replace({
            pathname: '/error/[code]',
            params: {
              code: '404',
              origin: 'AnimalDetails',
              message: `Animal with ID ${animalId?.[0]} not found`
            }
          })
        } else {
          setAnimal(result)
        }
      } catch (err: any) {
        console.error('Fetch error:', err)

        if (err?.response?.status === 401) {
          router.replace({
            pathname: '/error/[code]',
            params: { code: '401', origin: 'AnimalDetails' }
          })
        } else {
          router.replace({
            pathname: '/error/[code]',
            params: { code: '500', origin: 'AnimalDetails' }
          })
        }
      } finally {
        setLoading(false)
      }
    }

    if (!authLoading) fetchAnimal()
  }, [animalId, user, authLoading])

  return (
    <BaseComponent isLoading={authLoading || loading}>
      <AnimalDetailsContent animal={animal} />
    </BaseComponent>
  )
}