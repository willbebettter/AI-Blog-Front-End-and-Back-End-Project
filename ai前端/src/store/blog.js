import { defineStore } from 'pinia'
import { getBlogList, getBlogById, createBlog, updateBlog, deleteBlog, getComments, addComment, deleteComment } from '@/api/blog'

export const useBlogStore = defineStore('blog', {
  state: () => ({
    blogs: [],
    currentBlog: null,
    comments: []
  }),

  getters: {
    blogList: (state) => state.blogs,
    currentBlogDetail: (state) => state.currentBlog,
    commentList: (state) => state.comments
  },

  actions: {
    async fetchBlogList(params) {
      const res = await getBlogList(params)
      this.blogs = res.data || []
    },

    async fetchBlogById(id) {
      const res = await getBlogById(id)
      this.currentBlog = res.data
      return res.data
    },

    async createBlogAction(blog) {
      const res = await createBlog(blog)
      this.blogs.unshift(res.data)
      return res.data
    },

    async updateBlogAction(id, blog) {
      const res = await updateBlog(id, blog)
      const index = this.blogs.findIndex(b => b.id === id)
      if (index !== -1) {
        this.blogs[index] = res.data
      }
      if (this.currentBlog?.id === id) {
        this.currentBlog = res.data
      }
      return res.data
    },

    async deleteBlogAction(id) {
      await deleteBlog(id)
      this.blogs = this.blogs.filter(b => b.id !== id)
      if (this.currentBlog?.id === id) {
        this.currentBlog = null
      }
    },

    async fetchComments(blogId) {
      const res = await getComments(blogId)
      this.comments = res.data || []
    },

    async addCommentAction(comment) {
      const res = await addComment(comment)
      this.comments.push(res.data)
      return res.data
    },

    async deleteCommentAction(commentId) {
      await deleteComment(commentId)
      this.comments = this.comments.filter(c => c.id !== commentId)
    }
  }
})
