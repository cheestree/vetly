export const ApiPaths = {
    root: 'api/',
    users: {
        get: (id: string) => `users/${id}`,
    },
    animals: {
        root: '/animals',

        get: (id: string) => `/animals/${id}`,
        create: '/animals',
        update: (id: string) => `/animals/${id}`,
        delete: (id: string) => `/animals/${id}`,

        get_user_animals: '/animals/self',
        get_all: '/animals'
    },
    requests: {
        root: '/requests',

        get: (id: string) => `/requests/${id}`,
        create: '/requests',
        update: (id: string) => `/requests/${id}`,
        delete: (id: string) => `/requests/${id}`,

        get_user_animals: '/requests/self',
        get_all: '/requests'
    }
}