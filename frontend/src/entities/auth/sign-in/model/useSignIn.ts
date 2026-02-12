import { useForm } from '@inertiajs/react'
import { normalizeEmail, validateEmail } from '../lib/validateEmail'
import { validatePassword } from '../lib/validatePassword'

type SignInDTO = { email: string, password: string }

export const useSignIn = () => {
  const form = useForm<SignInDTO>({
    email: '',
    password: '',
  })

  const submit = () => {
    const errors: Record<string, string> = {}

    const emailNormalized = normalizeEmail(form.data.email)
    const emailErrors = validateEmail(emailNormalized)
    if (emailErrors) errors.email = emailErrors

    const pwdErrors = validatePassword(form.data.password)
    if (pwdErrors) errors.password = pwdErrors

    if (Object.keys(errors).length > 0) {
      form.setError(errors)
      return
    }

    form.clearErrors()
    form.setData('email', emailNormalized)

    form.post('/users/sign_in', {
      onSuccess: () => {
        form.reset()
        localStorage.setItem('IS_NEW_USER', 'true')
      },
      preserveScroll: true,
    })
  }

  return { form,
    submit }
}
