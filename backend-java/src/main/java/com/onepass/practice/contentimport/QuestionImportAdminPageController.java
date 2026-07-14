package com.onepass.practice.contentimport;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class QuestionImportAdminPageController {

    @GetMapping(value = "/admin/import/questions", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String page() {
        return """
                <!doctype html>
                <html lang="zh-CN">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>内容导入</title>
                  <style>
                    :root {
                      --bg: #f7f7f4;
                      --panel: #ffffff;
                      --ink: #1f2933;
                      --muted: #667085;
                      --line: #d9ded7;
                      --accent: #25634a;
                      --accent-soft: #e8f3ed;
                      --danger: #b42318;
                      --danger-soft: #fef2f2;
                      --warn: #a16207;
                      --warn-soft: #fffbeb;
                      --ok: #166534;
                      --neutral: #4b5563;
                    }
                    * { box-sizing: border-box; }
                    body {
                      margin: 0;
                      background: var(--bg);
                      color: var(--ink);
                      font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
                      line-height: 1.5;
                    }
                    main {
                      max-width: 1180px;
                      margin: 0 auto;
                      padding: 28px 20px 48px;
                    }
                    h1, h2, h3, p { margin-top: 0; }
                    h1 {
                      margin-bottom: 6px;
                      font-size: 30px;
                      letter-spacing: 0;
                    }
                    h2 {
                      margin-bottom: 16px;
                      font-size: 20px;
                    }
                    h3 {
                      margin-bottom: 10px;
                      font-size: 16px;
                    }
                    .sub {
                      margin-bottom: 0;
                      color: var(--muted);
                    }
                    .topbar {
                      display: flex;
                      justify-content: space-between;
                      gap: 16px;
                      align-items: end;
                      margin-bottom: 18px;
                    }
                    .auth {
                      width: min(440px, 100%);
                      display: grid;
                      grid-template-columns: 1fr auto;
                      gap: 10px;
                      align-items: end;
                    }
                    .tabs {
                      display: inline-flex;
                      gap: 4px;
                      padding: 4px;
                      margin-bottom: 18px;
                      border: 1px solid var(--line);
                      border-radius: 8px;
                      background: #eef1ed;
                    }
                    .tab-button {
                      min-width: 120px;
                      border-radius: 6px;
                      background: transparent;
                      color: var(--neutral);
                    }
                    .tab-button.active {
                      background: var(--panel);
                      color: var(--accent);
                      box-shadow: 0 1px 2px rgba(16, 24, 40, 0.08);
                    }
                    .grid {
                      display: grid;
                      grid-template-columns: 360px 1fr;
                      gap: 16px;
                      align-items: start;
                    }
                    .stack {
                      display: flex;
                      flex-direction: column;
                      gap: 16px;
                    }
                    .panel {
                      background: var(--panel);
                      border: 1px solid var(--line);
                      border-radius: 8px;
                      padding: 18px;
                    }
                    label {
                      display: block;
                      margin: 12px 0 6px;
                      font-size: 13px;
                      color: #374151;
                    }
                    input[type="text"], input[type="password"], input[type="file"], select {
                      width: 100%;
                      min-height: 42px;
                      border: 1px solid var(--line);
                      border-radius: 6px;
                      background: #fff;
                      padding: 9px 10px;
                      color: var(--ink);
                      font: inherit;
                    }
                    button {
                      border: 0;
                      border-radius: 6px;
                      padding: 10px 14px;
                      background: var(--accent);
                      color: #fff;
                      font: inherit;
                      cursor: pointer;
                    }
                    button.secondary { background: #eef1ed; color: #26352d; }
                    button.danger { background: var(--danger-soft); color: var(--danger); }
                    button:disabled { opacity: 0.5; cursor: not-allowed; }
                    .actions {
                      display: flex;
                      flex-wrap: wrap;
                      gap: 10px;
                      margin-top: 16px;
                    }
                    .stats {
                      display: grid;
                      grid-template-columns: repeat(5, minmax(92px, 1fr));
                      gap: 10px;
                      margin: 14px 0;
                    }
                    .stats.vocabulary {
                      grid-template-columns: repeat(4, minmax(92px, 1fr));
                    }
                    .stat {
                      width: 100%;
                      border: 1px solid var(--line);
                      border-radius: 8px;
                      padding: 12px;
                      background: #fbfcfa;
                      color: var(--ink);
                      text-align: left;
                    }
                    .stat.active {
                      border-color: var(--accent);
                      background: var(--accent-soft);
                    }
                    .stat strong {
                      display: block;
                      font-size: 24px;
                      line-height: 1.1;
                    }
                    .status {
                      display: inline-flex;
                      border-radius: 999px;
                      padding: 3px 9px;
                      font-size: 12px;
                      background: var(--accent-soft);
                      color: var(--ok);
                    }
                    .status.ERROR { background: var(--danger-soft); color: var(--danger); }
                    .status.WARNING { background: var(--warn-soft); color: var(--warn); }
                    .status.UNSUPPORTED, .status.CANCELED { background: #f3f4f6; color: var(--neutral); }
                    .status.IMPORTED { background: var(--accent-soft); color: var(--ok); }
                    table {
                      width: 100%;
                      border-collapse: collapse;
                      background: #fff;
                    }
                    th, td {
                      border-bottom: 1px solid var(--line);
                      padding: 10px;
                      text-align: left;
                      vertical-align: top;
                      font-size: 14px;
                    }
                    th {
                      background: #f2f4f1;
                      font-weight: 600;
                    }
                    details { max-width: 520px; }
                    summary {
                      cursor: pointer;
                      color: var(--accent);
                    }
                    .preview {
                      max-width: 360px;
                      color: #374151;
                    }
                    .detail-block {
                      margin-top: 10px;
                      padding: 12px;
                      border: 1px solid var(--line);
                      border-radius: 8px;
                      background: #fbfcfa;
                    }
                    .detail-title {
                      margin: 10px 0 4px;
                      color: var(--muted);
                      font-size: 13px;
                      font-weight: 600;
                    }
                    .mono {
                      white-space: pre-wrap;
                      word-break: break-word;
                    }
                    .option-list {
                      margin: 0;
                      padding-left: 18px;
                    }
                    .answer {
                      color: var(--ok);
                      font-weight: 700;
                    }
                    .message {
                      min-height: 22px;
                      margin-top: 10px;
                      color: var(--muted);
                    }
                    .message.error { color: var(--danger); }
                    .category-list {
                      margin-top: 12px;
                      max-height: 180px;
                      overflow: auto;
                      border: 1px solid var(--line);
                      border-radius: 8px;
                      background: #fbfcfa;
                    }
                    .category-row {
                      display: flex;
                      justify-content: space-between;
                      gap: 10px;
                      padding: 9px 12px;
                      border-bottom: 1px solid var(--line);
                      font-size: 13px;
                    }
                    .category-row:last-child { border-bottom: 0; }
                    .category-count { color: var(--muted); white-space: nowrap; }
                    [hidden] { display: none !important; }
                    @media (max-width: 900px) {
                      .topbar, .grid { display: block; }
                      .auth { margin-top: 14px; }
                      .panel { margin-bottom: 14px; }
                      .stats, .stats.vocabulary { grid-template-columns: repeat(2, 1fr); }
                    }
                  </style>
                </head>
                <body>
                  <main>
                    <section class="topbar">
                      <div>
                        <h1>内容导入</h1>
                        <p class="sub">题库和词库分开导入，均先预览再确认。</p>
                      </div>
                      <div class="auth">
                        <div>
                          <label for="token">管理 Token</label>
                          <input id="token" type="password" placeholder="APP_ADMIN_IMPORT_TOKEN" />
                        </div>
                        <button class="secondary" type="button" id="saveToken">保存 Token</button>
                      </div>
                    </section>

                    <nav class="tabs" aria-label="导入类型">
                      <button class="tab-button active" type="button" data-tab="questions">题库导入</button>
                      <button class="tab-button" type="button" data-tab="vocabulary">词库导入</button>
                    </nav>

                    <section id="questionsPanel" class="tab-panel">
                      <div class="grid">
                        <div class="stack">
                          <form class="panel" id="questionUploadForm">
                            <h2>题库导入</h2>
                            <label for="categoryPath">默认导入分类</label>
                            <select id="categoryPath"></select>
                            <label for="questionFile">题库 Docx 文件</label>
                            <input id="questionFile" type="file" accept=".docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document" />
                            <div class="actions">
                              <button type="submit">上传并解析</button>
                              <button class="secondary" type="button" id="refreshCategories">刷新分类</button>
                            </div>
                            <div id="questionMessage" class="message"></div>
                          </form>

                          <section class="panel">
                            <h2>分类管理</h2>
                            <label for="categoryCreatePath">新增分类路径</label>
                            <input id="categoryCreatePath" type="text" placeholder="例如：考研/政治/2010真题" />
                            <label for="categorySubtitle">分类副标题</label>
                            <input id="categorySubtitle" type="text" placeholder="默认：叶子分类" />
                            <div class="actions">
                              <button type="button" id="createCategory">新增分类</button>
                            </div>
                            <label for="deleteCategoryId">删除空叶子分类</label>
                            <select id="deleteCategoryId"></select>
                            <div class="actions">
                              <button class="danger" type="button" id="deleteCategory">删除分类</button>
                            </div>
                            <div id="categoryMessage" class="message"></div>
                            <div id="categoryList" class="category-list"></div>
                          </section>
                        </div>

                        <section class="panel">
                          <h2>题库批次预览</h2>
                          <div id="questionBatchMeta">暂无批次</div>
                          <div class="actions">
                            <button class="secondary" type="button" id="downloadMarkdown" disabled>下载 Markdown</button>
                            <button type="button" id="confirmQuestionImport" disabled>确认导入</button>
                            <button class="danger" type="button" id="cancelQuestionImport" disabled>取消批次</button>
                          </div>
                          <div id="questionItems"></div>
                        </section>
                      </div>
                    </section>

                    <section id="vocabularyPanel" class="tab-panel" hidden>
                      <div class="grid">
                        <form class="panel" id="vocabularyUploadForm">
                          <h2>词库导入</h2>
                          <label for="vocabularyFile">词库 Docx 文件</label>
                          <input id="vocabularyFile" type="file" accept=".docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document" />
                          <div class="actions">
                            <button type="submit">上传并解析</button>
                          </div>
                          <div id="vocabularyMessage" class="message"></div>
                        </form>

                        <section class="panel">
                          <h2>词库批次预览</h2>
                          <div id="vocabularyBatchMeta">暂无批次</div>
                          <div class="actions">
                            <button type="button" id="confirmVocabularyImport" disabled>确认导入</button>
                            <button class="danger" type="button" id="cancelVocabularyImport" disabled>取消批次</button>
                          </div>
                          <div id="vocabularyItems"></div>
                        </section>
                      </div>
                    </section>
                  </main>

                  <script>
                    const tokenInput = document.querySelector('#token');
                    const categorySelect = document.querySelector('#categoryPath');
                    const questionFileInput = document.querySelector('#questionFile');
                    const questionMessage = document.querySelector('#questionMessage');
                    const categoryCreatePath = document.querySelector('#categoryCreatePath');
                    const categorySubtitle = document.querySelector('#categorySubtitle');
                    const deleteCategorySelect = document.querySelector('#deleteCategoryId');
                    const categoryMessage = document.querySelector('#categoryMessage');
                    const categoryList = document.querySelector('#categoryList');
                    const questionBatchMeta = document.querySelector('#questionBatchMeta');
                    const questionItems = document.querySelector('#questionItems');
                    const downloadMarkdownButton = document.querySelector('#downloadMarkdown');
                    const confirmQuestionButton = document.querySelector('#confirmQuestionImport');
                    const cancelQuestionButton = document.querySelector('#cancelQuestionImport');
                    const vocabularyFileInput = document.querySelector('#vocabularyFile');
                    const vocabularyMessage = document.querySelector('#vocabularyMessage');
                    const vocabularyBatchMeta = document.querySelector('#vocabularyBatchMeta');
                    const vocabularyItems = document.querySelector('#vocabularyItems');
                    const confirmVocabularyButton = document.querySelector('#confirmVocabularyImport');
                    const cancelVocabularyButton = document.querySelector('#cancelVocabularyImport');

                    let currentQuestionBatchId = '';
                    let currentQuestionItems = [];
                    let activeQuestionFilter = 'all';
                    let currentVocabularyBatchId = '';
                    let currentVocabularyItems = [];
                    let activeVocabularyFilter = 'all';

                    tokenInput.value = localStorage.getItem('adminImportToken') || '';

                    function token() {
                      return tokenInput.value.trim();
                    }

                    function setMessage(element, text, error = false) {
                      element.textContent = text || '';
                      element.className = error ? 'message error' : 'message';
                    }

                    async function api(path, options = {}) {
                      const response = await fetch(path, {
                        ...options,
                        headers: {
                          ...(options.headers || {}),
                          'X-Admin-Import-Token': token()
                        }
                      });
                      const contentType = response.headers.get('content-type') || '';
                      const body = contentType.includes('application/json') ? await response.json() : await response.text();
                      if (!response.ok || body.success === false) {
                        throw new Error(body.message || body || '请求失败');
                      }
                      return body.data || body;
                    }

                    function escapeHtml(value) {
                      return String(value || '')
                        .replaceAll('&', '&amp;')
                        .replaceAll('<', '&lt;')
                        .replaceAll('>', '&gt;')
                        .replaceAll('"', '&quot;')
                        .replaceAll("'", '&#039;');
                    }

                    function renderReport(errors = [], warnings = []) {
                      const lines = [...errors, ...warnings].map(escapeHtml);
                      return lines.length ? lines.join('<br>') : '-';
                    }

                    document.querySelectorAll('.tab-button').forEach((button) => {
                      button.addEventListener('click', () => {
                        const tab = button.dataset.tab;
                        document.querySelectorAll('.tab-button').forEach((item) => {
                          item.classList.toggle('active', item.dataset.tab === tab);
                        });
                        document.querySelector('#questionsPanel').hidden = tab !== 'questions';
                        document.querySelector('#vocabularyPanel').hidden = tab !== 'vocabulary';
                      });
                    });

                    document.querySelector('#saveToken').addEventListener('click', () => {
                      localStorage.setItem('adminImportToken', token());
                      setMessage(questionMessage, 'Token 已保存。');
                      loadCategories().catch((error) => setMessage(categoryMessage, error.message, true));
                    });

                    async function loadCategories() {
                      if (!token()) {
                        setMessage(categoryMessage, '填写并保存 Token 后可加载分类。');
                        categorySelect.innerHTML = '<option value="">请先填写 Token</option>';
                        deleteCategorySelect.innerHTML = '<option value="">请先填写 Token</option>';
                        return;
                      }
                      const data = await api('/api/admin/import/categories');
                      renderCategories(data);
                      setMessage(categoryMessage, '分类已刷新。');
                    }

                    function renderCategories(data) {
                      const leafCategories = data.leafCategories || [];
                      const previousPath = categorySelect.value || '考研/政治/基础阶段';
                      categorySelect.innerHTML = leafCategories.map((category) => `
                        <option value="${escapeHtml(category.path)}">${escapeHtml(category.path)}（${category.questionCount}题）</option>
                      `).join('');
                      if (!leafCategories.length) {
                        categorySelect.innerHTML = '<option value="">暂无叶子分类</option>';
                      }
                      const matchedOption = Array.from(categorySelect.options).find((option) => option.value === previousPath);
                      if (matchedOption) {
                        categorySelect.value = previousPath;
                      }

                      const deletable = leafCategories.filter((category) => category.questionCount === 0);
                      deleteCategorySelect.innerHTML = deletable.map((category) => `
                        <option value="${escapeHtml(category.id)}">${escapeHtml(category.path)}</option>
                      `).join('');
                      if (!deletable.length) {
                        deleteCategorySelect.innerHTML = '<option value="">暂无可删除空分类</option>';
                      }

                      categoryList.innerHTML = leafCategories.map((category) => `
                        <div class="category-row">
                          <span>${escapeHtml(category.path)}</span>
                          <span class="category-count">${category.questionCount}题</span>
                        </div>
                      `).join('') || '<div class="category-row"><span>暂无分类</span></div>';
                    }

                    document.querySelector('#refreshCategories').addEventListener('click', () => {
                      loadCategories().catch((error) => setMessage(categoryMessage, error.message, true));
                    });

                    document.querySelector('#createCategory').addEventListener('click', async () => {
                      try {
                        const path = categoryCreatePath.value.trim();
                        if (!path) {
                          throw new Error('请填写新增分类路径');
                        }
                        await api('/api/admin/import/categories', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/json' },
                          body: JSON.stringify({
                            path,
                            subtitle: categorySubtitle.value.trim()
                          })
                        });
                        categoryCreatePath.value = '';
                        categorySubtitle.value = '';
                        await loadCategories();
                        categorySelect.value = path;
                        setMessage(categoryMessage, '分类已新增。');
                      } catch (error) {
                        setMessage(categoryMessage, error.message, true);
                      }
                    });

                    document.querySelector('#deleteCategory').addEventListener('click', async () => {
                      try {
                        const categoryId = deleteCategorySelect.value;
                        if (!categoryId) {
                          throw new Error('暂无可删除空分类');
                        }
                        const label = deleteCategorySelect.options[deleteCategorySelect.selectedIndex].text;
                        if (!confirm(`确认删除分类：${label}？`)) {
                          return;
                        }
                        const data = await api(`/api/admin/import/categories/${categoryId}`, { method: 'DELETE' });
                        renderCategories(data);
                        setMessage(categoryMessage, '分类已删除。');
                      } catch (error) {
                        setMessage(categoryMessage, error.message, true);
                      }
                    });

                    document.querySelector('#questionUploadForm').addEventListener('submit', async (event) => {
                      event.preventDefault();
                      try {
                        if (!token()) {
                          throw new Error('请填写管理 Token');
                        }
                        if (!questionFileInput.files.length) {
                          throw new Error('请选择题库 docx 文件');
                        }
                        if (!categorySelect.value) {
                          throw new Error('请选择导入分类');
                        }
                        const formData = new FormData();
                        formData.append('file', questionFileInput.files[0]);
                        formData.append('categoryPath', categorySelect.value);
                        setMessage(questionMessage, '正在上传并解析...');
                        const batch = await api('/api/admin/import/questions/docx', {
                          method: 'POST',
                          body: formData
                        });
                        renderQuestionBatch(batch);
                        setMessage(questionMessage, '解析完成，确认前不会写入题库。');
                      } catch (error) {
                        setMessage(questionMessage, error.message, true);
                      }
                    });

                    function renderQuestionBatch(batch) {
                      currentQuestionBatchId = batch.batchId;
                      currentQuestionItems = batch.items || [];
                      activeQuestionFilter = 'all';
                      downloadMarkdownButton.disabled = false;
                      confirmQuestionButton.disabled = batch.status === 'IMPORTED' || batch.status === 'CANCELED';
                      cancelQuestionButton.disabled = batch.status === 'IMPORTED' || batch.status === 'CANCELED';
                      questionBatchMeta.innerHTML = `
                        <p><strong>${escapeHtml(batch.originalFilename)}</strong> <span class="status ${batch.status}">${escapeHtml(batch.status)}</span></p>
                        <p class="sub">${escapeHtml(batch.batchId)}</p>
                        ${renderQuestionStats(batch)}
                      `;
                      renderQuestionItems();
                    }

                    function renderQuestionStats(batch) {
                      return `
                        <div class="stats" aria-label="题块筛选">
                          ${renderQuestionStatButton('all', batch.totalCount, '总题块')}
                          ${renderQuestionStatButton('importable', batch.supportedCount, '可导入')}
                          ${renderQuestionStatButton('unsupported', batch.unsupportedCount, '暂不支持')}
                          ${renderQuestionStatButton('error', batch.errorCount, '错误')}
                          ${renderQuestionStatButton('warning', batch.warningCount, '警告')}
                        </div>
                      `;
                    }

                    function renderQuestionStatButton(filter, count, label) {
                      const active = activeQuestionFilter === filter ? ' active' : '';
                      return `<button class="stat question-stat${active}" type="button" data-filter="${filter}"><strong>${count}</strong>${label}</button>`;
                    }

                    questionBatchMeta.addEventListener('click', (event) => {
                      const button = event.target.closest('.question-stat');
                      if (!button) return;
                      activeQuestionFilter = button.dataset.filter || 'all';
                      questionBatchMeta.querySelectorAll('.question-stat').forEach((item) => {
                        item.classList.toggle('active', item.dataset.filter === activeQuestionFilter);
                      });
                      renderQuestionItems();
                    });

                    function renderQuestionItems() {
                      const filteredItems = currentQuestionItems.filter(matchesQuestionFilter);
                      const rows = filteredItems.map((item) => `
                        <tr>
                          <td>${item.itemOrder}</td>
                          <td>${escapeHtml(item.sourceQuestionNo || '-')}</td>
                          <td>${escapeHtml(item.questionType || '-')}</td>
                          <td><span class="status ${item.status}">${escapeHtml(item.status)}</span></td>
                          <td>${escapeHtml(item.categoryPath || '-')}</td>
                          <td class="preview">
                            <details>
                              <summary>${escapeHtml(item.stemPreview || '查看详情')}</summary>
                              ${renderQuestionItemDetail(item)}
                            </details>
                          </td>
                          <td>${renderReport(item.errors || [], item.warnings || [])}</td>
                        </tr>
                      `).join('');
                      questionItems.innerHTML = `
                        <p class="sub">当前筛选：${questionFilterLabel(activeQuestionFilter)}，共 ${filteredItems.length} 个题块</p>
                        <table>
                          <thead>
                            <tr><th>#</th><th>原题号</th><th>题型</th><th>状态</th><th>分类</th><th>预览</th><th>报告</th></tr>
                          </thead>
                          <tbody>${rows || '<tr><td colspan="7">当前筛选下暂无题块</td></tr>'}</tbody>
                        </table>
                      `;
                    }

                    function matchesQuestionFilter(item) {
                      if (activeQuestionFilter === 'importable') {
                        return item.status === 'READY' || item.status === 'WARNING';
                      }
                      if (activeQuestionFilter === 'unsupported') {
                        return item.status === 'UNSUPPORTED';
                      }
                      if (activeQuestionFilter === 'error') {
                        return (item.errors || []).length > 0 || item.status === 'ERROR';
                      }
                      if (activeQuestionFilter === 'warning') {
                        return (item.warnings || []).length > 0;
                      }
                      return true;
                    }

                    function questionFilterLabel(filter) {
                      return {
                        all: '全部',
                        importable: '可导入',
                        unsupported: '暂不支持',
                        error: '错误',
                        warning: '警告'
                      }[filter] || '全部';
                    }

                    function renderQuestionItemDetail(item) {
                      const options = item.options || [];
                      const answerKeys = item.answerKeys || [];
                      return `
                        <div class="detail-block">
                          <div class="detail-title">题干</div>
                          <div class="mono">${escapeHtml(item.stem || '-')}</div>
                          <div class="detail-title">选项</div>
                          <ol class="option-list">
                            ${options.map((option) => `<li><strong>${escapeHtml(option.key || '')}.</strong> ${escapeHtml(option.content || '')}</li>`).join('') || '<li>-</li>'}
                          </ol>
                          <div class="detail-title">答案</div>
                          <div class="answer">${escapeHtml(answerKeys.join(', ') || '-')}</div>
                          <div class="detail-title">解析</div>
                          <div class="mono">${escapeHtml(item.analysis || '-')}</div>
                          <div class="detail-title">知识点 / 难易度</div>
                          <div>${escapeHtml(item.knowledgePoint || '-')} / ${escapeHtml(item.difficulty || '-')}</div>
                          <div class="detail-title">目标题目 ID</div>
                          <div>${escapeHtml(item.targetQuestionId || '-')}</div>
                        </div>
                      `;
                    }

                    downloadMarkdownButton.addEventListener('click', async () => {
                      if (!currentQuestionBatchId) return;
                      try {
                        const response = await fetch(`/api/admin/import/questions/${currentQuestionBatchId}/markdown?token=${encodeURIComponent(token())}`);
                        if (!response.ok) {
                          throw new Error(await response.text() || 'Markdown 下载失败');
                        }
                        const markdown = await response.text();
                        const blob = new Blob([markdown], { type: 'text/markdown;charset=utf-8' });
                        const url = URL.createObjectURL(blob);
                        const link = document.createElement('a');
                        link.href = url;
                        link.download = `questions-${currentQuestionBatchId}.md`;
                        document.body.appendChild(link);
                        link.click();
                        link.remove();
                        URL.revokeObjectURL(url);
                        setMessage(questionMessage, 'Markdown 已下载。');
                      } catch (error) {
                        setMessage(questionMessage, error.message, true);
                      }
                    });

                    confirmQuestionButton.addEventListener('click', async () => {
                      if (!currentQuestionBatchId || !confirm('确认导入可支持题型到正式题库？')) return;
                      try {
                        const batch = await api(`/api/admin/import/questions/${currentQuestionBatchId}/confirm`, { method: 'POST' });
                        renderQuestionBatch(batch);
                        setMessage(questionMessage, '题库导入完成。');
                      } catch (error) {
                        setMessage(questionMessage, error.message, true);
                      }
                    });

                    cancelQuestionButton.addEventListener('click', async () => {
                      if (!currentQuestionBatchId || !confirm('确认取消本次题库导入？')) return;
                      try {
                        const batch = await api(`/api/admin/import/questions/${currentQuestionBatchId}/cancel`, { method: 'POST' });
                        renderQuestionBatch(batch);
                        setMessage(questionMessage, '题库批次已取消。');
                      } catch (error) {
                        setMessage(questionMessage, error.message, true);
                      }
                    });

                    document.querySelector('#vocabularyUploadForm').addEventListener('submit', async (event) => {
                      event.preventDefault();
                      try {
                        if (!token()) {
                          throw new Error('请填写管理 Token');
                        }
                        if (!vocabularyFileInput.files.length) {
                          throw new Error('请选择词库 docx 文件');
                        }
                        const formData = new FormData();
                        formData.append('file', vocabularyFileInput.files[0]);
                        setMessage(vocabularyMessage, '正在上传并解析...');
                        const batch = await api('/api/admin/import/vocabulary/docx', {
                          method: 'POST',
                          body: formData
                        });
                        renderVocabularyBatch(batch);
                        setMessage(vocabularyMessage, '解析完成，确认前不会写入词库。');
                      } catch (error) {
                        setMessage(vocabularyMessage, error.message, true);
                      }
                    });

                    function renderVocabularyBatch(batch) {
                      currentVocabularyBatchId = batch.batchId;
                      currentVocabularyItems = batch.items || [];
                      activeVocabularyFilter = 'all';
                      confirmVocabularyButton.disabled = batch.status === 'IMPORTED'
                        || batch.status === 'CANCELED'
                        || batch.errorCount > 0
                        || batch.importableCount === 0;
                      cancelVocabularyButton.disabled = batch.status === 'IMPORTED' || batch.status === 'CANCELED';
                      vocabularyBatchMeta.innerHTML = `
                        <p><strong>${escapeHtml(batch.originalFilename)}</strong> <span class="status ${batch.status}">${escapeHtml(batch.status)}</span></p>
                        <p class="sub">${escapeHtml(batch.batchId)}</p>
                        <p>${escapeHtml(batch.bookName || '-')} / ${escapeHtml(batch.bookId || '-')}</p>
                        ${renderVocabularyStats(batch)}
                        ${renderReport(batch.errors || [], batch.warnings || []) !== '-' ? `<p class="message error">${renderReport(batch.errors || [], batch.warnings || [])}</p>` : ''}
                      `;
                      renderVocabularyItems();
                    }

                    function renderVocabularyStats(batch) {
                      return `
                        <div class="stats vocabulary" aria-label="单词筛选">
                          ${renderVocabularyStatButton('all', batch.totalCount, '总单词')}
                          ${renderVocabularyStatButton('importable', batch.importableCount, '可导入')}
                          ${renderVocabularyStatButton('error', batch.errorCount, '错误')}
                          ${renderVocabularyStatButton('warning', batch.warningCount, '警告')}
                        </div>
                      `;
                    }

                    function renderVocabularyStatButton(filter, count, label) {
                      const active = activeVocabularyFilter === filter ? ' active' : '';
                      return `<button class="stat vocabulary-stat${active}" type="button" data-filter="${filter}"><strong>${count}</strong>${label}</button>`;
                    }

                    vocabularyBatchMeta.addEventListener('click', (event) => {
                      const button = event.target.closest('.vocabulary-stat');
                      if (!button) return;
                      activeVocabularyFilter = button.dataset.filter || 'all';
                      vocabularyBatchMeta.querySelectorAll('.vocabulary-stat').forEach((item) => {
                        item.classList.toggle('active', item.dataset.filter === activeVocabularyFilter);
                      });
                      renderVocabularyItems();
                    });

                    function renderVocabularyItems() {
                      const filteredItems = currentVocabularyItems.filter(matchesVocabularyFilter);
                      const rows = filteredItems.map((item) => `
                        <tr>
                          <td>${item.itemOrder}</td>
                          <td>${escapeHtml(item.english || '-')}</td>
                          <td>${escapeHtml(item.chinese || '-')}</td>
                          <td>${escapeHtml(item.partOfSpeech || '-')}</td>
                          <td>${item.sortOrder}</td>
                          <td><span class="status ${item.status}">${escapeHtml(item.status)}</span></td>
                          <td>${renderReport(item.errors || [], item.warnings || [])}</td>
                        </tr>
                      `).join('');
                      vocabularyItems.innerHTML = `
                        <p class="sub">当前筛选：${vocabularyFilterLabel(activeVocabularyFilter)}，共 ${filteredItems.length} 个单词</p>
                        <table>
                          <thead>
                            <tr><th>#</th><th>英文</th><th>中文</th><th>词性</th><th>排序</th><th>状态</th><th>报告</th></tr>
                          </thead>
                          <tbody>${rows || '<tr><td colspan="7">当前筛选下暂无单词</td></tr>'}</tbody>
                        </table>
                      `;
                    }

                    function matchesVocabularyFilter(item) {
                      if (activeVocabularyFilter === 'importable') {
                        return item.status === 'READY' || item.status === 'WARNING';
                      }
                      if (activeVocabularyFilter === 'error') {
                        return (item.errors || []).length > 0 || item.status === 'ERROR';
                      }
                      if (activeVocabularyFilter === 'warning') {
                        return (item.warnings || []).length > 0;
                      }
                      return true;
                    }

                    function vocabularyFilterLabel(filter) {
                      return {
                        all: '全部',
                        importable: '可导入',
                        error: '错误',
                        warning: '警告'
                      }[filter] || '全部';
                    }

                    confirmVocabularyButton.addEventListener('click', async () => {
                      if (!currentVocabularyBatchId || !confirm('确认导入词库到正式词库表？')) return;
                      try {
                        const batch = await api(`/api/admin/import/vocabulary/${currentVocabularyBatchId}/confirm`, { method: 'POST' });
                        renderVocabularyBatch(batch);
                        setMessage(vocabularyMessage, '词库导入完成。');
                      } catch (error) {
                        setMessage(vocabularyMessage, error.message, true);
                      }
                    });

                    cancelVocabularyButton.addEventListener('click', async () => {
                      if (!currentVocabularyBatchId || !confirm('确认取消本次词库导入？')) return;
                      try {
                        const batch = await api(`/api/admin/import/vocabulary/${currentVocabularyBatchId}/cancel`, { method: 'POST' });
                        renderVocabularyBatch(batch);
                        setMessage(vocabularyMessage, '词库批次已取消。');
                      } catch (error) {
                        setMessage(vocabularyMessage, error.message, true);
                      }
                    });

                    if (token()) {
                      loadCategories().catch((error) => setMessage(categoryMessage, error.message, true));
                    } else {
                      renderCategories({ leafCategories: [] });
                    }
                  </script>
                </body>
                </html>
                """;
    }
}
