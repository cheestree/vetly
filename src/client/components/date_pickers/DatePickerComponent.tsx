import React, { FC } from 'react';
import { Platform, View } from 'react-native';
import MobileDatePicker from './MobileDateTimePicker';
import WebDatePicker from './WebDateTimePicker';

interface DatePickerComponentProps {
  selectedDate: string | null
  onDateChange: (date: string | null) => void
}

export default function DatePickerComponent ({ selectedDate, onDateChange }: DatePickerComponentProps) {
  return (
    <View>
      {Platform.OS === 'web' ? (
        <WebDatePicker selectedDate={selectedDate} onDateChange={onDateChange} />
      ) : (
        <MobileDatePicker selectedDate={selectedDate} onDateChange={onDateChange} />
      )}
    </View>
  )
}