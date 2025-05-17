import { Toast } from 'toastify-react-native';

ErrorUtils.setGlobalHandler((error, isFatal) => {
    // Format and show the error
    console.error("Global Error:", error)

    Toast.show({
        type: 'error',
        text1: error,
        useModal: false
    })
})