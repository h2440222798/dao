export interface BaseResponse<T> {
  code: number
  data: T
  message: string
}

export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
}

const API_PREFIX = '/api'

function buildQuery(params?: Record<string, unknown>) {
  if (!params) return ''

  const searchParams = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') return
    searchParams.append(key, String(value))
  })
  const query = searchParams.toString()
  return query ? `?${query}` : ''
}

async function request<T>(path: string, init: RequestInit = {}) {
  const response = await fetch(`${API_PREFIX}${path}`, {
    credentials: 'include',
    ...init,
    headers: {
      ...(init.body ? { 'Content-Type': 'application/json' } : {}),
      ...(init.headers ?? {})
    }
  })

  let result: BaseResponse<T>
  try {
    result = await response.json()
  } catch {
    throw new Error('接口返回格式错误')
  }

  if (!response.ok || result.code !== 0) {
    throw new Error(result.message || '请求失败')
  }

  return result.data
}

export function apiGet<T>(path: string, params?: Record<string, unknown>) {
  return request<T>(`${path}${buildQuery(params)}`)
}

export function apiPost<T>(path: string, body?: unknown, params?: Record<string, unknown>) {
  return request<T>(`${path}${buildQuery(params)}`, {
    method: 'POST',
    body: body === undefined ? undefined : JSON.stringify(body)
  })
}

export function normalizeDate(value: unknown) {
  if (!value) return ''
  const text = String(value)
  if (text.includes('T')) {
    const [datePart] = text.split('T')
    return datePart || text
  }
  return text
}

export function parseJsonArray(value: unknown): string[] {
  if (!value) return []
  if (Array.isArray(value)) {
    return value.map((item) => String(item))
  }
  if (typeof value === 'string') {
    try {
      const parsed = JSON.parse(value)
      return Array.isArray(parsed) ? parsed.map((item) => String(item)) : []
    } catch {
      return value ? [value] : []
    }
  }
  return []
}

export function parseJsonValue<T>(value: unknown, fallback: T): T {
  if (value === undefined || value === null || value === '') return fallback
  if (typeof value === 'string') {
    try {
      return JSON.parse(value) as T
    } catch {
      return fallback
    }
  }
  return value as T
}

export function stringifyJson(value: unknown) {
  return JSON.stringify(value ?? [])
}
