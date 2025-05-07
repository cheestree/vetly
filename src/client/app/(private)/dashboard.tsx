import React from 'react';
import { View, Text } from 'react-native';
import Title from 'expo-router/head'

export default function Dashboard(params) {
    return ( 
        <>
            <Title>Dashboard</Title>        
            <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
                <Text>Dashboard</Text>
            </View>
        </>
    ) 
};
