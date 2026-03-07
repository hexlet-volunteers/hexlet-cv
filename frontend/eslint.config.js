import js from '@eslint/js'
import react from 'eslint-plugin-react'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'
import { globalIgnores } from 'eslint/config'
import stylistic from '@stylistic/eslint-plugin'
import fsdPlugin from 'eslint-plugin-fsd-lint'
import eslintConfigPrettier from 'eslint-config-prettier'

export default tseslint.config([
  {
    ignores: [
      'node_modules/',
      'static/',
      '.cache/',
      '*.config.js',
      '**/*.d.ts',
      '**/toDelete/',
    ],
  },
  globalIgnores(['dist']),

  js.configs.recommended,
  ...tseslint.configs.recommended,
  stylistic.configs.recommended,
  fsdPlugin.configs.recommended,
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: {
        ...globals.browser,
        ...globals.es2020,
      },
    },
    ignores: ['**/*.test.ts'],
    plugins: {
      react,
      fsd: fsdPlugin,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      'fsd/forbidden-imports': 'error',
      'fsd/no-relative-imports': 'error',
      'fsd/no-public-api-sidestep': 'error',
      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': ['warn', { allowConstantExport: true }],
    },
  },
  eslintConfigPrettier,
])
