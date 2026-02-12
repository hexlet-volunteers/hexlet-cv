import type { User } from '@shared/types/inertiaSharedData'

type State = {
  user: User | null
}

const state: State = {
  user: null,
}

export const authStore = {
  getUser(): User | null {
    return state.user
  },
  isAuthed(): boolean {
    return Boolean(state.user)
  },
  setUser(user: User | null) {
    state.user = user
  },
  reset() {
    state.user = null
  },
}
