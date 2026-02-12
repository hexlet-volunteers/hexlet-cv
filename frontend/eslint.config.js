import js from '@eslint/js'
import react from 'eslint-plugin-react'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'
import { globalIgnores } from 'eslint/config'
import stylistic from '@stylistic/eslint-plugin'
import fsdPlugin from 'eslint-plugin-fsd-lint'

export default tseslint.config([
  {
    ignores: [
      'node_modules/',
      'static/',
      '.cache/',
      '*.config.js',
      '**/*.d.ts',
      '**/toDelete/'
    ],
  },
  stylistic.configs.recommended,
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs['recommended-latest'],
      reactRefresh.configs.vite,
      fsdPlugin.configs.recommended,
    ],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    ignores: ['**/*.test.ts'],
    plugins: { react, fsd: fsdPlugin },
    rules: {
      'fsd/forbidden-imports': 'error',
      'fsd/no-relative-imports': 'error',
      'fsd/no-public-api-sidestep': 'error',
      'indent': ['error', 2,
        {
          SwitchCase: 1,
          VariableDeclarator: 1,
          MemberExpression: 1,
          FunctionDeclaration: { body: 1, parameters: 2 },
          FunctionExpression: { body: 1, parameters: 2 },
          CallExpression: { arguments: 1 },
        }],
      'array-element-newline': ['error', 'consistent'],
      'object-property-newline': ['error', { allowAllPropertiesOnSameLine: false }],
      'block-spacing': ['error', 'always'],
      'brace-style': ['error', '1tbs', { allowSingleLine: true }],
      'comma-spacing': ['error', { before: false, after: true }],
      'key-spacing': ['error', { beforeColon: false, afterColon: true }],
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'warn',
      'space-before-blocks': ['error', 'always'],
      'space-infix-ops': ['error', { int32Hint: false }],
      'no-unused-vars': 'warn',
      '@stylistic/comma-dangle': [
        'error',
        {
          arrays: 'always-multiline',
          objects: 'always-multiline',
          functions: 'never',
        },
      ],
    },
  },
])
