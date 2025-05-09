import { View, Text } from 'react-native';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';
import CheckupServices from '@/api/services/CheckupServices';

export default function CheckupDetails() {
  const { checkupId } = useLocalSearchParams();
  const [checkup, setCheckup] = useState<CheckupInformation>()

  useEffect(() => {
    const fetchCheckup = async () => {
      const checkup = await CheckupServices.getCheckup(checkupId[0])
      if (checkup){
        setCheckup(checkup)
      } else {
        console.error("Error fetching user profile:", checkup)
      }
    }

    fetchCheckup()
  }, [checkupId])

  return (
    <View style={{ flex: 1, padding: 16 }}>
      <Text style={{ fontSize: 24, marginBottom: 16 }}>Checkup Details</Text>
      <Text>Viewing details for checkup #{checkup?.id}</Text>
    </View>
  );
}