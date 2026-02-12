export type MockDbUser = {
  id: number
  email: string
  password: string
  roles: string[]
}

const users: MockDbUser[] = [
  { id: 1,
    email: 'demo@site.com',
    password: 'qwerty',
    roles: ['user'] },
]

export const mockDbUsers = {
  users,
  findByEmail(email: string) {
    return users.find(u => u.email.toLowerCase() === email.toLowerCase())
  },
  findById(id: number) {
    return users.find(u => u.id === id) ?? null
  },
  createUser(email: string, password: string) {
    const id = users.length + 1
    const u = { id,
      email,
      password,
      roles: ['user'] }
    users.push(u)
    return u
  },
}
