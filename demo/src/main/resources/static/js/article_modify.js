let email = null;
let articleId = null;
let lastCategoryId = null;
// 标签管理逻辑
const tagsInput = document.querySelector('.tags-input');
const tagInput = tagsInput.querySelector('input');

tagInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault();
        const value = tagInput.value.trim();
        if (value) {
            const tag = document.createElement('div');
            tag.className = 'tag';
            tag.innerHTML = `
                        <span>${value}</span>
                        <span class="tag-remove">×</span>
                    `;
            tagsInput.insertBefore(tag, tagInput);
            tagInput.value = '';
        }
    }
});

// 删除标签
tagsInput.addEventListener('click', (e) => {
    if (e.target.classList.contains('tag-remove')) {
        e.target.parentElement.remove();
    }
});

// 表单提交
document.getElementById('articleForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const checkedRadio = document.querySelector('input[name="visibility"]:checked');
    const formData = {
        title: document.querySelector('input[id="title"]').value,
        categoryId: document.getElementById('categorySelect').value,
        tags: Array.from(document.querySelectorAll('.tag span:first-child'))
            .map(tag => tag.textContent),
        content: document.querySelector('.editor-content').innerHTML,
        visibility: checkedRadio.value.trim()
    };

    const response = await fetch(`/api/articles/article_editor/modify?email=${encodeURIComponent(email)}&articleId=${encodeURIComponent(articleId)}&lastCategoryId=${encodeURIComponent(lastCategoryId)}`,{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    });

    const data = await response.json();
    if (response.ok) {
        alert("文章修改成功");
        window.location.href = `dashboard.html?email=${email}`; // 跳转到列表页
    } else {
        alert(data.message); // 显示错误提示
    }
});

window.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    email = urlParams.get('email') || null;
    articleId = urlParams.get('articleId') || null;
    if (!email) {
        alert("请先登录");
        window.location.href = 'login.html';
        return;
    }
    if (!articleId) {
        alert("文章ID不能为空");
        window.location.href = `hashboard.html?email=${encodeURIComponent(email)}`;
        return;
    }
    const select = document.getElementById('categorySelect');
    const response = await fetch(`/api/categories?email=${encodeURIComponent(email)}`);
    if (!response.ok) alert("分类获得失败，请稍后再试");
    const categories = await response.json();
    const fragment = document.createDocumentFragment();

    const buildOptions = (items, level = 0) => {
        items.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;       // 对应DTO的categoryId
            option.textContent = ' '.repeat(level) + cat.name; // 对应DTO的categoryName
            // 处理子分类
            if (cat.children?.length) {
                fragment.appendChild(option);
                buildOptions(cat.children, level + 1);
            } else {
                fragment.appendChild(option);
            }
        });
    };

    buildOptions(categories);
    select.appendChild(fragment);

    const response2 = await fetch(`/api/articles/article_editor?articleId=${encodeURIComponent(articleId)}`,{
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    });
    if (!response2.ok) alert("内容获得失败，请稍后再试");
    const article = await response2.json();
    if (article) {
        document.querySelector('input[id="title"]').value = article.data.title;
        document.querySelector('.editor-content').innerHTML = article.data.content;
        document.querySelector(`input[name="visibility"][value="${article.data.visibility}"]`).checked = true;

        // 设置分类选择
        const categorySelect = document.getElementById('categorySelect');
        categorySelect.value = article.data.categoryId || '';
        lastCategoryId = article.data.categoryId || '';
        // 设置标签
        article.data.tags.forEach(tag => {
            const tagDiv = document.createElement('div');
            tagDiv.className = 'tag';
            tagDiv.innerHTML = `<span>${tag}</span><span class="tag-remove">×</span>`;
            tagsInput.insertBefore(tagDiv, tagInput);
        });
    }
});