import { AdminUsers, type UsersDTO } from '@widgets/admin-users/'
import { AdminLayout } from '../components/AdminLayout'
import type React from 'react'

interface Users {
  users: UsersDTO[]
}

const Users = ({ users }: Users) => {
  return <AdminUsers users={users} />
}

Users.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default Users
