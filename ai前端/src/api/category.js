import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/blog'

export const fetchCategories = async () => {
  const res = await getCategories()
  return res.data
}

export const createCategoryAction = async (name, description) => {
  const res = await createCategory({ name, description })
  return res.data
}

export const updateCategoryAction = async (id, name, description) => {
  const res = await updateCategory(id, { name, description })
  return res.data
}

export const deleteCategoryAction = async (id) => {
  const res = await deleteCategory(id)
  return res.data
}
