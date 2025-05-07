import { View, Text, Pressable, StyleSheet, Platform } from 'react-native'
import { useRouter } from 'expo-router'


export default function Header() {
  const router = useRouter()

  const navItems = [
    { label: 'Vetly', route: '/' },
    { label: 'About', route: '/about' },
    { label: 'Contacts', route: '/contact' },
    { label: 'Login', route: '/login' }
  ]

  return (
    <View style={styles.header}>
      <View style={styles.navbar}>
        {navItems.map((item) => (
          <Pressable
            key={item.route}
            onPress={() => router.push(item.route)}
            style={({ hovered }) => [
              styles.link,
              hovered && styles.linkHover
            ]}
          >
            <Text style={styles.text}>{item.label}</Text>
          </Pressable>
        ))}
      </View>
    </View>
  )
}

const styles = StyleSheet.create({
  header: {
    backgroundColor: '#f8f8f8',
    padding: Platform.OS === 'ios' ? 32 : 16,
    borderRadius: 16,
    borderBottomColor: '#ccc',
    borderBottomWidth: 1,
  },
  navbar: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    marginStart: 16,
  },
  link: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 16
  },
  linkHover: {
    backgroundColor: '#e0e0e0'
  },
  text: {
    fontSize: 16,
    fontWeight: '500'
  }
})

