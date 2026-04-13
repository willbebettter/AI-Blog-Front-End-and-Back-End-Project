import api from '@/utils/api'

export const getBlogList = (params) => api.get('/blog', { params }).then(res => res.data)
export const getBlogById = (id, userId) => {
  const params = userId ? { userId } : {}
  return api.get(`/blog/${id}`, { params }).then(res => res.data)
}
export const createBlog = (data) => api.post('/blog', data).then(res => res.data)
export const updateBlog = (id, data) => api.put(`/blog/${id}`, data).then(res => res.data)
export const deleteBlog = (id) => api.delete(`/blog/${id}`).then(res => res.data)
export const searchBlogs = (keyword, page = 1, size = 10, sortBy = 'createTime', order = 'DESC') => api.get('/blog/search', { params: { keyword, page, size, sortBy, order } }).then(res => res.data)
export const toggleLike = (id, userId) => api.post(`/blog/${id}/like`, { userId }).then(res => res.data)
export const toggleCollect = (id, userId, action) => api.post(`/blog/${id}/collect`, { userId, action }).then(res => res.data)
export const getPopularBlogs = (limit) => api.get('/blog/popular', { params: { limit } }).then(res => res.data)
export const getBlogsByTag = (tag, page = 1, size = 10, sortBy = 'createTime', order = 'DESC') => api.get(`/blog/tag/${tag}`, { params: { page, size, sortBy, order } }).then(res => res.data)
export const getBlogsByAuthor = (authorId) => api.get(`/blog/author/${authorId}`).then(res => res.data)
export const getBlogStats = () => api.get('/blog/stats').then(res => res.data)
export const isBlogLiked = (blogId, userId) => api.get(`/blog/${blogId}/is-liked`, { params: { userId } }).then(res => res.data)
export const isBlogCollected = (blogId, userId) => api.get(`/blog/${blogId}/is-collected`, { params: { userId } }).then(res => res.data)

export const getComments = (blogId) => api.get(`/comment/blog/${blogId}`).then(res => res.data)
export const addComment = (data) => api.post('/comment', data).then(res => res.data)
export const deleteComment = (id) => api.delete(`/comment/${id}`).then(res => res.data)

export const getCategories = () => api.get('/category').then(res => res.data)
export const getCategoryById = (id) => api.get(`/category/${id}`).then(res => res.data)
export const createCategory = (data) => api.post('/category', data).then(res => res.data)
export const updateCategory = (id, data) => api.put(`/category/${id}`, data).then(res => res.data)
export const deleteCategory = (id) => api.delete(`/category/${id}`).then(res => res.data)
