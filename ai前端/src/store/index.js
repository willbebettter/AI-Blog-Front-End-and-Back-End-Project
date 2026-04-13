import { defineStore } from 'pinia'
import { blogApi, commentApi } from './blog'

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
      const res = await blogApi.getBlogList(params)
      this.blogs = res.data || []
    },

    async fetchBlogById(id) {
      const res = await blogApi.getBlogById(id)
      this.currentBlog = res.data
      return res.data
    },

    async createBlog(blog) {
      const res = await blogApi.createBlog(blog)
      this.blogs.unshift(res.data)
      return res.data
    },

    async updateBlog(id, blog) {
      const res = await blogApi.updateBlog(id, blog)
      const index = this.blogs.findIndex(b => b.id === id)
      if (index !== -1) {
        this.blogs[index] = res.data
      }
      if (this.currentBlog?.id === id) {
        this.currentBlog = res.data
      }
      return res.data
    },

    async deleteBlog(id) {
      await blogApi.deleteBlog(id)
      this.blogs = this.blogs.filter(b => b.id !== id)
      if (this.currentBlog?.id === id) {
        this.currentBlog = null
      }
    },

    async fetchComments(blogId) {
      const res = await commentApi.getComments(blogId)
      this.comments = res.data || []
    },

    async addComment(comment) {
      const res = await commentApi.addComment(comment)
      this.comments.push(res.data)
      return res.data
    },

    async deleteComment(commentId) {
      await commentApi.deleteComment(commentId)
      this.comments = this.comments.filter(c => c.id !== commentId)
    }
  }
})
