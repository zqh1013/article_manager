// 编辑器实例和用户信息
let editor = null;
let email = null;

// 初始化编辑器
function initEditor(content = '') {
    const { createEditor, createToolbar } = window.wangEditor;
    const editorConfig = {
        placeholder: '请输入内容...',
        onChange(editor) {
          console.log('内容变化：', editor.getHtml());
        }
    };

    // 7. 创建编辑器
    editor = createEditor({
        selector: '#editor-container',
        config: editorConfig,
        html: '<p>初始内容</p>'
    });

    // 8. 创建工具栏（可选）
    createToolbar({
        editor,
        selector: '#editor-container',
        config: {
//            toolbarKeys: [
//                'bold', 'italic', 'underline', '|',
//                'fontSize', 'fontFamily', 'color', '|',
//                'bulletedList', 'numberedList', '|',
//                'image', 'table', '|',
//                'undo', 'redo'
//            ]
            toolbarKeys: [
                'fontSize', 'fontFamily'
            ]
        }
    });
}

// 标签管理
function setupTags() {
  const tagsInput = document.querySelector('.tags-input');
  const tagInput = tagsInput.querySelector('input');

  tagInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      const value = tagInput.value.trim();
      if (value) {
        const tag = document.createElement('div');
        tag.className = 'tag';
        tag.innerHTML = `<span>${value}</span><span class="tag-remove">×</span>`;
        tagsInput.insertBefore(tag, tagInput);
        tagInput.value = '';
      }
    }
  });

  tagsInput.addEventListener('click', (e) => {
    if (e.target.classList.contains('tag-remove')) {
      e.target.parentElement.remove();
    }
  });
}

// 表单提交
function setupFormSubmit() {
  document.getElementById('articleForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const checkedRadio = document.querySelector('input[name="visibility"]:checked');
    const formData = {
      title: document.getElementById('title').value,
      categoryId: document.getElementById('categorySelect').value,
      tags: Array.from(document.querySelectorAll('.tag span:first-child')).map(t => t.textContent),
      content: editor.getHtml(),
      visibility: checkedRadio.value
    };

    try {
      const response = await fetch(`/api/articles/article_editor?email=${encodeURIComponent(email)}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        alert('保存成功');
        window.location.href = `dashboard.html?email=${email}`;
      } else {
        const error = await response.json();
        alert(error.message || '保存失败');
      }
    } catch (err) {
      alert('网络错误: ' + err.message);
    }
  });
}

// 加载分类数据
async function loadCategories() {
  try {
    const response = await fetch(`/api/categories?email=${encodeURIComponent(email)}`);
    const categories = await response.json();
    const select = document.getElementById('categorySelect');

    const buildOptions = (items, level = 0) => {
      items.forEach(cat => {
        const option = document.createElement('option');
        option.value = cat.id;
        option.textContent = ' '.repeat(level) + cat.name;
        select.appendChild(option);
        if (cat.children?.length) buildOptions(cat.children, level + 1);
      });
    };

    buildOptions(categories);
  } catch (err) {
    console.error('加载分类失败:', err);
  }
}

// 页面初始化
window.addEventListener('DOMContentLoaded', async () => {
  // 获取用户信息
  const urlParams = new URLSearchParams(window.location.search);
  email = urlParams.get('email');
  if (!email) {
    alert('请先登录');
    window.location.href = 'login.html';
    return;
  }

  // 初始化各组件
  initEditor();
  setupTags();
  setupFormSubmit();
  await loadCategories();
});