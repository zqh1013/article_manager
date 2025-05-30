// 全局变量
let categories = null;
let currentPage = 1;
const articlesPerPage = 9; // 每页显示9篇文章（3x3网格）
let totalArticles = 0;
let currentFilter = {
    keyword: '',
    category: '',
    startDate: '',
    endDate: ''
};
let pageData = null;

// 从API获取文章数据
async function loadArticles(page) {
    try {
        // 显示加载状态
        document.getElementById('articleListContainer').innerHTML = `
            <div class="col-span-3 flex justify-center py-12">
                <div class="loading-spinner"></div>
                <p class="ml-4 text-gray-600">正在加载文章数据...</p>
            </div>
        `;

        // 构建查询参数
        const params = new URLSearchParams({
            page: page,
            limit: articlesPerPage,
            email: email
        });

        // API请求-从数据库加载文章来填充文章列表
        try {
            const response = await fetch(`/api/articles?${params.toString()}`);
            if (!response.ok) {
                alert(response.status)
                throw new Error(`API请求失败: ${response.status}`);
            }
            pageData = await response.json();
        } catch (error) {
            console.error('获取文章数据失败:', error);
            const data = {
                total: 0,
                page: 1,
                limit: articlesPerPage,
                data: []
            };
        }

        // 更新全局变量
        totalArticles = pageData.total;
        currentPage = page;

        // 渲染文章列表
        renderArticles(pageData.data);

        // 渲染分页组件
        renderPagination();

    } catch (error) {
        console.error('加载文章失败:', error);
        document.getElementById('articleListContainer').innerHTML = `
            <div class="col-span-3 text-center py-12">
                <i class="fas fa-exclamation-triangle text-yellow-500 text-4xl mb-4"></i>
                <p class="text-gray-600">加载文章失败，请稍后再试</p>
                <button onclick="loadArticles(currentPage)" class="mt-4 bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700">
                    重新加载
                </button>
            </div>
        `;
    }
}

// 渲染文章列表
function renderArticles(articles) {
    const container = document.getElementById('articleListContainer');

    if (articles.length === 0) {
        container.innerHTML = `
            <div class="col-span-3 text-center py-12">
                <i class="fas fa-file-alt text-gray-400 text-4xl mb-4"></i>
                <p class="text-gray-600">没有找到符合条件的文章</p>
                <button onclick="createArticle()" class="mt-4 bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700">
                    创建新文章
                </button>
            </div>
        `;
        return;
    }
    let html = '';

    articles.forEach(article => {
        // 根据可见性设置图标和文本
        const visibilityIcon = article.visibility === 'public'
            ? '<i class="fas fa-globe-asia mr-1"></i>'
            : '<i class="fas fa-lock mr-1"></i>';

        const visibilityText = article.visibility === 'public'
            ? '公开共享'
            : '私密';

        const visibilityClass = article.visibility === 'public'
            ? 'text-green-600'
            : '';

        html += `
        <div class="card-hover bg-white rounded-lg shadow-md overflow-visible flex flex-col">
            <div class="p-5 flex-grow">
                <div class="flex justify-between items-start mb-1">
                    <a href="article_view.html?id=${article.id}" class="text-lg font-semibold text-gray-900 hover:text-indigo-600 leading-tight">
                        ${article.title}
                    </a>
                    <div class="relative article-menu">
                        <button class="text-gray-400 hover:text-gray-600 focus:outline-none p-1 -mr-1 article-menu-button">
                            <i class="fas fa-ellipsis-v"></i>
                        </button>
                        <div class="dropdown-menu hidden origin-top-right right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 z-20">
                            <a href="article_editor.html?id=${article.id}" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                <i class="fas fa-edit w-4 mr-2"></i>编辑
                            </a>
                            <a href="javascript:void(0);" onclick="deleteArticle(${article.id})" class="block px-4 py-2 text-sm text-red-600 hover:bg-red-50">
                                <i class="fas fa-trash w-4 mr-2"></i>删除
                            </a>
                            <a href="javascript:moveArticleToCategory(${article.id})" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                <i class="fas fa-folder-tree w-4 mr-2"></i>移动分类
                            </a>
                            <a href="javascript:toggleArticleVisibility(${article.id}, '${article.visibility === 'public' ? 'private' : 'public'}')" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                <i class="fas ${article.visibility === 'public' ? 'fa-lock' : 'fa-share-alt'} w-4 mr-2"></i>
                                ${article.visibility === 'public' ? '设为私密' : '设为共享'}
                            </a>
                        </div>
                    </div>
                </div>
                <p class="mt-1 text-sm text-gray-600 leading-relaxed line-clamp-3">
                    摘要暂无
                </p>
            </div>
            <div class="bg-gray-50 px-5 py-3 border-t border-gray-100">
                <div class="flex items-center justify-between text-xs text-gray-500">
                    <div>
                        <span class="${getCategoryColorClass(article.category_id)} px-2 py-0.5 rounded-full font-medium">
                            ${article.category_name}
                        </span>
                        <span class="ml-2 ${visibilityClass}">
                            ${visibilityIcon} ${visibilityText}
                        </span>
                    </div>
                    <span>${formatDate(article.create_time)}</span>
                </div>
            </div>
        </div>
        `;
    });

    container.innerHTML = html;

    // 绑定文章菜单事件
    bindArticleMenuEvents();
}

// 渲染分页组件
function renderPagination() {
    const totalPages = Math.ceil(totalArticles / articlesPerPage);
    const container = document.getElementById('paginationContainer');

    let html = `
        <div class="hidden sm:block">
            <p class="text-sm text-gray-700">
                显示第 <span class="font-medium">${(currentPage - 1) * articlesPerPage + 1}</span>
                到 <span class="font-medium">${Math.min(currentPage * articlesPerPage, totalArticles)}</span> 条，
                共 <span class="font-medium">${totalArticles}</span> 条结果
            </p>
        </div>
        <div class="flex-1 flex justify-between sm:justify-end">
    `;

    // 上一页按钮
    html += `
        <button onclick="changePage(${currentPage - 1})"
            ${currentPage === 1 ? 'disabled' : ''}
            class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 ${currentPage === 1 ? 'opacity-50 cursor-not-allowed' : ''}">
            上一页
        </button>
    `;

    // 页码按钮
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
        html += `
            <button onclick="changePage(${i})"
                class="relative inline-flex items-center px-4 py-2 border text-sm font-medium mx-1 rounded-md
                ${i === currentPage ? 'bg-indigo-600 text-white border-indigo-600' : 'border-gray-300 text-gray-700 bg-white hover:bg-gray-50'}">
                ${i}
            </button>
        `;
    }

    // 下一页按钮
    html += `
        <button onclick="changePage(${currentPage + 1})"
            ${currentPage === totalPages ? 'disabled' : ''}
            class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 ${currentPage === totalPages ? 'opacity-50 cursor-not-allowed' : ''}">
            下一页
        </button>
    `;

    html += `</div>`;
    container.innerHTML = html;
}

// 切换页面
function changePage(page) {
    const totalPages = Math.ceil(totalArticles / articlesPerPage);

    if (page < 1 || page > totalPages) return;

    currentPage = page;
    loadArticles(page);

    // 滚动到顶部
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// 应用筛选条件
function applyFilters() {
    currentFilter = {
        keyword: document.getElementById('filter-keyword').value,
        category: document.getElementById('filter-category').value,
        startDate: document.getElementById('filter-date-start').value,
        endDate: document.getElementById('filter-date-end').value
    };

    // 重置到第一页
    currentPage = 1;
    loadArticles(currentPage);
}

// 重置筛选条件
function resetFilters() {
    document.getElementById('filter-keyword').value = '';
    document.getElementById('filter-category').value = '';
    document.getElementById('filter-date-start').value = '';
    document.getElementById('filter-date-end').value = '';

    currentFilter = {
        keyword: '',
        category: '',
        startDate: '',
        endDate: ''
    };

    // 重置到第一页
    currentPage = 1;
    loadArticles(currentPage);
}

// 删除文章
function deleteArticle(articleId) {
    if (confirm('确定删除这篇文章吗？')) {
        // 模拟API请求
        // fetch(`/api/articles/${articleId}`, { method: 'DELETE' })
        //   .then(() => loadArticles(currentPage));

        // 模拟删除成功
        setTimeout(() => {
            alert(`文章 #${articleId} 已删除`);
            loadArticles(currentPage);
        }, 500);
    }
}

// 切换文章可见性
function toggleArticleVisibility(articleId, newVisibility) {
    // 模拟API请求
    // fetch(`/api/articles/${articleId}/visibility`, {
    //   method: 'PUT',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ visibility: newVisibility })
    // }).then(() => loadArticles(currentPage));

    // 模拟更新成功
    setTimeout(() => {
        alert(`文章 #${articleId} 已设为 ${newVisibility === 'public' ? '公开' : '私密'}`);
        loadArticles(currentPage);
    }, 500);
}

// 移动文章到其他分类
function moveArticleToCategory(articleId) {
    // 实际实现中，这里应该弹出一个分类选择对话框
    const newCategoryId = prompt('请输入新的分类ID:');
    if (newCategoryId) {
        // 模拟API请求
        // fetch(`/api/articles/${articleId}/category`, {
        //   method: 'PUT',
        //   headers: { 'Content-Type': 'application/json' },
        //   body: JSON.stringify({ category_id: newCategoryId })
        // }).then(() => loadArticles(currentPage));

        // 模拟更新成功
        setTimeout(() => {
            alert(`文章 #${articleId} 已移动到分类 #${newCategoryId}`);
            loadArticles(currentPage);
        }, 500);
    }
}

// 辅助函数：获取分类颜色类
function getCategoryColorClass(categoryId) {
    const colors = {
        1: 'bg-purple-100 text-purple-700',
        2: 'bg-blue-100 text-blue-700',
        3: 'bg-green-100 text-green-700',
        4: 'bg-yellow-100 text-yellow-700'
    };

    return colors[categoryId] || 'bg-gray-100 text-gray-700';
}

// 辅助函数：格式化日期
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
}

// 重新绑定文章菜单事件
function bindArticleMenuEvents() {
    // 文章卡片菜单下拉
    document.querySelectorAll('.article-menu-button').forEach(button => {
        button.addEventListener('click', function(event) {
            // 关闭其他打开的菜单
            document.querySelectorAll('.article-menu .dropdown-menu').forEach(m => {
                if (m !== this.nextElementSibling) m.classList.add('hidden');
            });
            this.nextElementSibling.classList.toggle('hidden');
            event.stopPropagation();
        });
    });

    // 点击其他地方关闭文章菜单
    document.body.addEventListener('click', function(event) {
        document.querySelectorAll('.article-menu .dropdown-menu').forEach(menu => {
            if (!menu.parentElement.contains(event.target)) {
                menu.classList.add('hidden');
            }
        });
    });
}


// 新建文章函数
function createArticle() {
    window.location.href = `article_editor.html?email=${email}`;
}


// 移动端菜单切换
const mobileMenuButton = document.querySelector('.mobile-menu-button');
const mobileMenu = document.getElementById('mobile-menu');
mobileMenuButton.addEventListener('click', function() {
    const expanded = this.getAttribute('aria-expanded') === 'true' || false;
    this.setAttribute('aria-expanded', !expanded);
    mobileMenu.classList.toggle('hidden');
    const icon = this.querySelector('i');
    icon.classList.toggle('fa-bars');
    icon.classList.toggle('fa-times');
});


// 文章卡片菜单下拉
document.querySelectorAll('.article-menu-button').forEach(button => {
    button.addEventListener('click', function(event) {
        // Close other open menus
        document.querySelectorAll('.article-menu .dropdown-menu').forEach(m => {
            if (m !== this.nextElementSibling) m.classList.add('hidden');
        });
        this.nextElementSibling.classList.toggle('hidden');
        event.stopPropagation(); // Prevent body click from closing it immediately
    });
});

//--------------------------分类编辑---------------------------------------------------------

// 渲染分类树
function renderCategoryTreeList(containerId, categories, level = 0) {
    const container = document.getElementById(containerId);
    return container.innerHTML = categories.map(category => renderCategoryTree(category,level)).join('');
}
function renderCategoryTree(category, level) {
    return`
            <div class="category-item group ${category.isOpen ? 'open' : ''}" data-id="${category.id}">
                <div class="flex items-center justify-between p-2 hover:bg-gray-100 rounded cursor-pointer"
                     onclick="toggleCategory(${category.id})">
                    <div class="flex items-center">
                        <i class="fas ${category.children.length ? 'fa-folder-open' : 'fa-folder'}
                            text-yellow-500 mr-2"></i>
                        <span>${category.name}</span>
                        <span class="text-gray-400 text-xs ml-2">(${category.articleCount})</span>
                    </div>
                    <div class="flex items-center space-x-2">
                        <button onclick="openEditModal(${category.id})"
                                class="text-blue-500 hover:text-blue-700">
                            <i class="fas fa-edit text-xs"></i>
                        </button>
                        <button onclick="deleteCategory(${category.id})"
                                class="text-red-500 hover:text-red-700">
                            <i class="fas fa-trash text-xs"></i>
                        </button>
                        ${category.children.length ?
        `<i class="arrow-icon ${category.isOpen ? 'rotated' : ''} fas fa-chevron-down text-gray-400 text-xs"></i>` : ''}
                    </div>
                </div>
                ${category.isOpen && category.children?.length ? `
                  <div class="sub-category ml-4">
                    ${category.children.map(child => renderCategoryTree(child, level + 1)).join('')}
                  </div>` : ''}
            </div>
        `;
}

// 分类操作函数
function toggleCategory(id) {
    const category = findCategory(categories, id);
    if (category) {
        category.isOpen = !category.isOpen;
        renderCategoryTreeList('categoryList', categories);

    }
}

// 修改分类
async function modifyCategory(newCategory) {
    await fetch(`/api/categories/modify?email=${encodeURIComponent(email)}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newCategory)
    });
    renderCategoryTreeList('categoryList', categories);

}

// 删除分类
async function deleteCategory(id) {
    if (!confirm('确定删除此分类及其所有子分类吗？')) return;
    await fetch(`/api/categories/${id}?email=${encodeURIComponent(email)}`,
                { method: 'DELETE' });
    await loadCategories();
}

//从数据库中加载分类
async function loadCategories() {
    const response = await fetch(`/api/categories?email=${encodeURIComponent(email)}`);
    categories = await response.json();
    renderCategoryTreeList('categoryList', categories);
}

// 保存新创建的分类
async function saveCategories(newCategory) {
    const response = await fetch(`/api/categories/add?email=${encodeURIComponent(email)}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newCategory)
    });
    //获得此时的id
    const data = await response.json();
    newCategory.id = data.id;
    renderCategoryTreeList('categoryList', categories);

}


// 分类管理模态框功能
let editingId = null;

function openCategoryModal() {
    document.getElementById('categoryModal').classList.remove('hidden');
    renderManageList();
    populateParentSelect();
}

function closeCategoryModal() {
    document.getElementById('categoryModal').classList.add('hidden');
    editingId = null;
}

async function handleAddCategory() {
    const nameInput = document.getElementById('categoryName');
    const parentSelect = document.getElementById('parentCategory');
    const newName = nameInput.value.trim();
    const newParentId = parentSelect.value ? Number(parentSelect.value) : null;

    if (editingId) {
        // ==================== 编辑逻辑 ====================
        const target = findCategory(categories, editingId);

        // 1. 验证父分类是否合法（不能设置自己为父分类）
        if (editingId === newParentId) {
            alert('不能选择自己作为父分类');
            return;
        }
        target.name = newName;
        target.isOpen = false;
        const oldParentId = target.parentId;
        target.parentId = newParentId;

        // 4. 如果修改了父分类，需要移动分类位置


        if (oldParentId !== newParentId) {

            // 从原位置移除
            removeCategoryFromParent(target.id, oldParentId);
            // 添加到新父分类
            if (newParentId) {
                const newParent = findCategory(categories, newParentId);
                newParent.children.push(target);

            } else {
                categories.push(target);

            }
        }
        await modifyCategory(target);
    } else {
        // 添加逻辑
        const newCategory = {
            id: null,
            name: nameInput.value.trim(),
            parentId: newParentId,
            articleCount: 0,
            isOpen: false,
            children: []
        };

        if (!newCategory.name) {
            alert('请输入分类名称');
            return;
        }
        if (newCategory.parentId) {
            const parent = findCategory(categories, newCategory.parentId);
            parent?.children.push(newCategory);
        } else {
            categories.push(newCategory);
        }
        await saveCategories(newCategory);
    }
    nameInput.value = '';
    closeCategoryModal();
}

// 辅助函数：从父分类中移除指定分类
function removeCategoryFromParent(categoryId, parentId) {
    if (parentId) {
        const parent = findCategory(categories, parentId);
        parent.children = parent.children.filter(c => c.id !== categoryId);
    } else {
        categories = categories.filter(c => c.id !== categoryId);
    }
}

// 辅助函数
function findCategory(list, id) {
    for (const item of list) {
        if (item.id === id) return item;
        if (item.children?.length) {
            const found = findCategory(item.children, id);
            if (found) {
                return found;
            }
        }
    }
    return null;
}

// 管理中填充父分类下拉列表
function populateParentSelect() {
    const select = document.getElementById('parentCategory');
    select.innerHTML = '<option value="">无父分类</option>';
    const traverse = (list, level = 0) => {
        list.forEach(cat => {
            select.innerHTML += `
                    <option value="${cat.id}">
                        ${'&nbsp;&nbsp;'.repeat(level)}${cat.name}
                    </option>
                `;
            if (cat.children?.length) traverse(cat.children, level + 1);
        });
    };
    traverse(categories);
}


//显示分类列表，包含编辑和删除按钮
function renderManageList() {
    const container = document.getElementById('categoryManageList');
    container.innerHTML = categories.map(cat => renderCategoryItem(cat)).join('');
}

function renderCategoryItem(category, level = 0) {
    return `
            <div class="flex items-center justify-between p-2 bg-gray-50 rounded">
                <span style="padding-left: ${level * 20}px">${category.name}</span>
                <div class="space-x-2">
                    <button onclick="openEditModal(${category.id})"
                            class="text-blue-500 hover:text-blue-700">
                        编辑
                    </button>
                    <button onclick="modalDeleteCategory(${category.id})"
                            class="text-red-500 hover:text-red-700">
                        删除
                    </button>
                </div>
            </div>
            ${category.children?.map(child => renderCategoryItem(child, level + 1)).join('')}
        `;
}
async function modalDeleteCategory(id) {
    await deleteCategory(id);
    openCategoryModal();
}
function openEditModal(id) {
    const category = findCategory(categories, id);
    if (!category) return;

    editingId = id;
    document.getElementById('categoryName').value = category.name;
    document.getElementById('parentCategory').value = category.parentId || '';
    openCategoryModal();
}
let email = null;
//新建文章函数
//"article_editor.html"
function createArticle(){
    window.location.href = `article_editor.html?email=${email}`
}

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    email = urlParams.get('email') || null;
    if(!email){
        alert("请先登录");
        window.location.href = 'login.html';
    }else {
        alert(email);
        loadCategories();

        loadArticles(currentPage);
        // 设置筛选按钮事件
        document.getElementById('applyFilterBtn').addEventListener('click', applyFilters);
        document.getElementById('resetFilterBtn').addEventListener('click', resetFilters);
    }
});

// Simulate admin link visibility
const isAdmin = true; // Change this to false to hide admin link
if (isAdmin) {
    document.getElementById('adminLinkContainer').style.display = 'block';
} else {
    document.getElementById('adminLinkContainer').style.display = 'none';
}

// 用户头像下拉菜单交互
const menuButton = document.getElementById('user-menu-button');
const dropdown = document.querySelector('.user-dropdown-menu');
menuButton.addEventListener('click', () => {
  const isExpanded = menuButton.getAttribute('aria-expanded') === 'true';
  menuButton.setAttribute('aria-expanded', !isExpanded);
  dropdown.style.display = isExpanded ? 'none' : 'block';
});