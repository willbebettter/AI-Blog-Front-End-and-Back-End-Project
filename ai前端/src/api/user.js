import api from '@/utils/api'

export const register = async (params) => {
  const response = await api.post('/user/register', params)
  return response.data
}

export const login = async (params) => {
  const response = await api.post('/user/login', params)
  return response.data
}

export const getUserInfo = async (userId) => {
  const response = await api.get(`/user/${userId}`)
  return response.data
}

export const updateUser = async (id, data) => {
  const response = await api.put(`/user/${id}`, data)
  return response.data
}

export const getUserBlogs = async (userId) => {
  const response = await api.get(`/user/${userId}/blogs`)
  return response.data
}

export const getLikedBlogs = async (userId) => {
  const response = await api.get(`/user/${userId}/liked-blogs`)
  return response.data
}

export const getCollectedBlogs = async (userId) => {
  const response = await api.get(`/user/${userId}/collected-blogs`)
  return response.data
}

export const getFollowedBlogs = async (userId) => {
  const response = await api.get(`/user/followed?userId=${userId}`)
  return response.data
}

export const followUser = async (userId, followingId) => {
  const response = await api.post('/follow', { userId, followingId })
  return response.data
}

export const unfollowUser = async (userId, followingId) => {
  const response = await api.post('/follow/unfollow', { userId, followingId })
  return response.data
}

export const getFollowing = async (userId) => {
  const response = await api.get(`/follow/${userId}/following`)
  return response.data
}

export const getFollowers = async (userId) => {
  const response = await api.get(`/follow/${userId}/followers`)
  return response.data
}

export const isFollowing = async (userId, targetId) => {
  const response = await api.get(`/follow/${userId}/is-following?targetId=${targetId}`)
  return response.data
}

export const getNotifications = async (userId) => {
  const response = await api.get(`/notification/${userId}`)
  return response.data
}

export const getUnreadCount = async (userId) => {
  const response = await api.get(`/notification/${userId}/unread-count`)
  return response.data
}

export const markNotificationRead = async (id) => {
  const response = await api.put(`/notification/${id}/read`)
  return response.data
}

export const markAllNotificationsRead = async (userId) => {
  const response = await api.put(`/notification/${userId}/read-all`)
  return response.data
}
