// TODO FE-01~04: 공통 API 명세와 구현 체크리스트를 보고 완성하세요.
let session = { accessToken: "", user: null };
export const getSession = () => session;
export function clearSession() {
  session = { accessToken: "", user: null };
}
function authorizationHeaders() {
  // TODO FE-02: 메모리에 보관한 토큰을 Bearer 헤더로 보내세요.
  // 명세/체크리스트 요구 사항: 토큰을 Authorization: Bearer <JWT> 형식의 인증 헤더로 생성하여 전송
  if (session.accessToken) {
    return { Authorization: `Bearer ${session.accessToken}` };
  }
  return {};
}
export async function request(path, options = {}) {
  const response = await fetch(`/api${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...authorizationHeaders(),
      ...options.headers,
    },
  });
  const data = response.status === 204 ? null : await response.json();
  if (!response.ok) {
    // TODO FE-03: 401이면 메모리의 로그인 상태도 해제하세요.
    // 명세/체크리스트 요구사항:401 응답 시 메모리의 로그인 상태를 해제
    if (response.status === 401) {
      clearSession();
    }
    const error = new Error(
        data.message || data.code || `HTTP ${response.status}`,
    );
    error.status = response.status;
    error.code = data.code;
    throw error;
  }
  return data;
}
export async function login(email, password) {
  // TODO FE-01: POST /auth/login → 응답의 토큰과 사용자 정보를 session에 저장.
  // request()를 이용할 수 있습니다. 성공하면 user를 반환하세요.
  // 명세/체크리스트 요구사항: 이메일·비밀번호를 JSON으로 전송하고 성공 시 accessToken과 user를 메모리에 저장

  const result = await request("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
  session.accessToken = result.accessToken;
  session.user = result.user;
  return result.user;
}
export function canEdit(post) {
  // TODO FE-04: 로그인 사용자와 작성자를 비교하세요. 서버 검사도 반드시 필요합니다.
  // 명세/체크리스트 요구사항: 로그인 사용자와 글 작성자(authorId)를 비교하여 수정 권한 확인
  if (!session.user || !post) return false;
  return session.user.id === post.authorId;
}
export const api = {
  list: () => request("/posts"),
  detail: (id) => request(`/posts/${id}`),
  create: (input) =>
      request("/posts", { method: "POST", body: JSON.stringify(input) }),
  update: (id, input) =>
      request(`/posts/${id}`, { method: "PUT", body: JSON.stringify(input) }),
  remove: (id) => request(`/posts/${id}`, { method: "DELETE" }),
  me: () => request("/me"),
};