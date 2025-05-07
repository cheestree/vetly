import React from 'react';
import { View, Text } from 'react-native';
import Title from 'expo-router/head'

export default function Checkups(params) {
    return (
        <>
            <Title>Checkups</Title>
            <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
                <Text>Checkups</Text>
            </View>
        </>
    )
};
