let email = null;
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

    const response = await fetch(`/api/articles/article_editor?email=${encodeURIComponent(email)}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    });

    const data = await response.json();
    if (response.ok) {
        alert("文章创建成功");
        window.location.href = `dashboard.html?email=${email}`; // 跳转到列表页
    } else {
        alert(1)
        alert(data.message); // 显示错误提示
    }
});

window.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    email = urlParams.get('email') || null;
    if (!email) {
        alert("请先登录");
        window.location.href = 'login.html';
        return;
    }
    const select = document.getElementById('categorySelect');
    const response = await fetch(`/api/categories?email=${encodeURIComponent(email)}`);
    if (!response.ok) alert("1")
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
});