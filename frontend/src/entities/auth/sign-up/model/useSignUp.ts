import { useForm } from '@inertiajs/react'
import { validatePassword } from '../lib/validatePassword'
import { normalizeName, validateName } from '../lib/validateName'
import { normalizeEmail, validateEmail } from '../lib/validateEmail'

type SignUpDTO = { email: string, firstName: string, lastName: string, password: string, terms: boolean }

export const useSignUp = () => {
  const form = useForm<SignUpDTO>({
    email: '',
    firstName: '',
    lastName: '',
    password: '',
    terms: true,
  })

  const submit = () => {
    const errors: Record<string, string> = {}

    const lastNameNormalized = normalizeName(form.data.lastName)
    const lastNameErr = validateName(lastNameNormalized)
    if (lastNameErr) errors.lastName = lastNameErr

    const firstNameNormalized = normalizeName(form.data.firstName)
    const firstNameErr = validateName(firstNameNormalized)
    if (firstNameErr) errors.firstName = firstNameErr

    const emailNormalized = normalizeEmail(form.data.email)
    const emailErrors = validateEmail(emailNormalized)
    if (emailErrors) errors.email = emailErrors

    const pwdErrors = validatePassword(form.data.password)
    if (pwdErrors) errors.password = pwdErrors

    if (!form.data.terms) errors.terms = 'terms.required'

    if (Object.keys(errors).length > 0) {
      form.setError(errors)
      return
    }

    form.clearErrors()
    form.setData('lastName', lastNameNormalized)
    form.setData('firstName', firstNameNormalized)
    form.setData('email', emailNormalized)

    form.post(`/users/sign_up`, {
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
