INSERT INTO student (id, display_name, avatar_url)
VALUES
    (1001, '微信用户', '')
ON DUPLICATE KEY UPDATE
    display_name = VALUES(display_name),
    avatar_url = VALUES(avatar_url);

INSERT INTO category (id, parent_id, name, subtitle, is_leaf, sort_order)
VALUES
    ('root-kaoyan', NULL, '考研', '必刷题', 0, 10),
    ('root-cert', NULL, '证书', '金融类', 0, 20),
    ('kaoyan-english', 'root-kaoyan', '英语', '考研方向', 0, 10),
    ('kaoyan-politics', 'root-kaoyan', '政治', '考研方向', 0, 20),
    ('cert-fund', 'root-cert', '基金从业', '资格考试', 0, 10),
    ('cert-teacher', 'root-cert', '教师资格', '资格考试', 0, 20),
    ('english-core', 'kaoyan-english', '核心词汇', '叶子分类', 1, 10),
    ('english-reading', 'kaoyan-english', '阅读理解', '叶子分类', 1, 20),
    ('politics-basic', 'kaoyan-politics', '基础阶段', '叶子分类', 1, 10),
    ('politics-advanced', 'kaoyan-politics', '强化阶段', '叶子分类', 1, 20),
    ('fund-subject-1', 'cert-fund', '科目一', '叶子分类', 1, 10),
    ('fund-subject-2', 'cert-fund', '科目二', '叶子分类', 1, 20),
    ('teacher-comprehensive', 'cert-teacher', '综合素质', '叶子分类', 1, 10),
    ('teacher-education', 'cert-teacher', '教育知识', '叶子分类', 1, 20)
ON DUPLICATE KEY UPDATE
    parent_id = VALUES(parent_id),
    name = VALUES(name),
    subtitle = VALUES(subtitle),
    is_leaf = VALUES(is_leaf),
    sort_order = VALUES(sort_order);

INSERT INTO question (id, category_id, question_type, stem, analysis, sort_order, status)
VALUES
    ('english-core-1', 'english-core', 'single', '以下哪个单词表示“放弃”？', 'abandon 表示“放弃”，是考研英语核心词汇中的高频考点。', 10, 1),
    ('english-core-2', 'english-core', 'multiple', '以下哪些单词可以作动词使用？', 'issue、abandon、maintain 都可作动词，requirement 通常作名词。', 20, 1),
    ('english-core-3', 'english-core', 'single', '下列哪个单词表示“区分”？', 'distinguish 表示“区分”，常见于阅读与写作搭配。', 30, 1),
    ('english-reading-1', 'english-reading', 'single', '阅读题中出现转折词 however 时，通常优先关注哪一部分信息？', 'however 常提示作者态度或主旨转折，转折后信息通常更关键。', 10, 1),
    ('english-reading-2', 'english-reading', 'multiple', '做英语阅读定位题时，以下哪些方法更有效？', '先看题干关键词、带着选项回文定位、关注同义替换，都比盲读全文更高效。', 20, 1),
    ('english-reading-3', 'english-reading', 'single', '如果题干问作者态度，通常优先寻找哪类信息？', '作者态度题优先关注评价词、转折词和情感色彩明显的表达。', 30, 1),
    ('politics-basic-1', 'politics-basic', 'single', '马克思主义哲学认为，物质和意识的关系中，哪一项表述正确？', '唯物论的基本立场是物质决定意识。', 10, 1),
    ('politics-basic-2', 'politics-basic', 'multiple', '下列哪些属于实践的基本特征？', '实践具有客观物质性、自觉能动性和社会历史性。', 20, 1),
    ('politics-basic-3', 'politics-basic', 'single', '真理的客观性是指：', '真理的内容来源于客观事物及其规律，不以人的意志为转移。', 30, 1),
    ('politics-advanced-1', 'politics-advanced', 'single', '新发展理念中，强调解决发展不平衡问题的是哪一项？', '协调发展主要对应解决发展不平衡问题。', 10, 1),
    ('politics-advanced-2', 'politics-advanced', 'multiple', '中国式现代化的重要特征包括哪些？', '中国式现代化强调人口规模巨大、共同富裕和人与自然和谐共生。', 20, 1),
    ('politics-advanced-3', 'politics-advanced', 'single', '共同富裕在社会主义本质要求中体现为：', '共同富裕强调全体人民共享发展成果，而不是固化贫富差距。', 30, 1),
    ('fund-subject-1-1', 'fund-subject-1', 'single', '基金管理人应当遵循的核心原则不包括下列哪一项？', '基金从业规范中不得向投资者随意承诺收益。', 10, 1),
    ('fund-subject-1-2', 'fund-subject-1', 'multiple', '以下哪些属于基金销售适当性管理的要求？', '适当性管理强调识别客户风险能力、匹配产品并充分揭示风险。', 20, 1),
    ('fund-subject-1-3', 'fund-subject-1', 'single', '基金托管人的核心职责之一是：', '基金托管人应依法保管基金财产，并监督管理人的投资运作。', 30, 1),
    ('teacher-comprehensive-1', 'teacher-comprehensive', 'single', '教师职业道德中最基本、最核心的要求是：', '关爱学生是教师职业道德的重要核心要求之一。', 10, 1),
    ('teacher-comprehensive-2', 'teacher-comprehensive', 'multiple', '下列哪些行为符合教师职业规范？', '体罚学生违反教师职业规范，其余三项符合要求。', 20, 1),
    ('teacher-comprehensive-3', 'teacher-comprehensive', 'single', '教师在处理课堂突发冲突时，优先应当：', '处理课堂冲突时应先稳定课堂秩序，再了解事实并进行教育引导。', 30, 1)
ON DUPLICATE KEY UPDATE
    category_id = VALUES(category_id),
    question_type = VALUES(question_type),
    stem = VALUES(stem),
    analysis = VALUES(analysis),
    sort_order = VALUES(sort_order),
    status = VALUES(status);

INSERT INTO question_option (question_id, option_key, content, sort_order)
VALUES
    ('english-core-1', 'A', 'issue', 10),
    ('english-core-1', 'B', 'abandon', 20),
    ('english-core-1', 'C', 'maintain', 30),
    ('english-core-1', 'D', 'require', 40),
    ('english-core-2', 'A', 'issue', 10),
    ('english-core-2', 'B', 'abandon', 20),
    ('english-core-2', 'C', 'maintain', 30),
    ('english-core-2', 'D', 'requirement', 40),
    ('english-core-3', 'A', 'distinguish', 10),
    ('english-core-3', 'B', 'consume', 20),
    ('english-core-3', 'C', 'constant', 30),
    ('english-core-3', 'D', 'approach', 40),
    ('english-reading-1', 'A', '转折词之前的铺垫', 10),
    ('english-reading-1', 'B', '转折词之后的真正观点', 20),
    ('english-reading-1', 'C', '段落中的专有名词', 30),
    ('english-reading-1', 'D', '所有数字信息', 40),
    ('english-reading-2', 'A', '先看题干关键词', 10),
    ('english-reading-2', 'B', '带着选项回文定位', 20),
    ('english-reading-2', 'C', '忽略转折句', 30),
    ('english-reading-2', 'D', '关注同义替换', 40),
    ('english-reading-3', 'A', '评价词和情感色彩明显的表达', 10),
    ('english-reading-3', 'B', '所有年份和数字', 20),
    ('english-reading-3', 'C', '段落长度变化', 30),
    ('english-reading-3', 'D', '专有名词的多少', 40),
    ('politics-basic-1', 'A', '意识决定物质', 10),
    ('politics-basic-1', 'B', '物质决定意识', 20),
    ('politics-basic-1', 'C', '二者没有联系', 30),
    ('politics-basic-1', 'D', '意识先于物质存在', 40),
    ('politics-basic-2', 'A', '客观物质性', 10),
    ('politics-basic-2', 'B', '主观随意性', 20),
    ('politics-basic-2', 'C', '自觉能动性', 30),
    ('politics-basic-2', 'D', '社会历史性', 40),
    ('politics-basic-3', 'A', '多数人认可就是真理', 10),
    ('politics-basic-3', 'B', '不以人的意志为转移', 20),
    ('politics-basic-3', 'C', '只在理论中成立', 30),
    ('politics-basic-3', 'D', '永远不需要发展', 40),
    ('politics-advanced-1', 'A', '创新', 10),
    ('politics-advanced-1', 'B', '协调', 20),
    ('politics-advanced-1', 'C', '绿色', 30),
    ('politics-advanced-1', 'D', '开放', 40),
    ('politics-advanced-2', 'A', '人口规模巨大', 10),
    ('politics-advanced-2', 'B', '全体人民共同富裕', 20),
    ('politics-advanced-2', 'C', '人与自然和谐共生', 30),
    ('politics-advanced-2', 'D', '照搬西方现代化道路', 40),
    ('politics-advanced-3', 'A', '少数人长期独享发展成果', 10),
    ('politics-advanced-3', 'B', '全体人民共享发展成果', 20),
    ('politics-advanced-3', 'C', '只追求经济总量', 30),
    ('politics-advanced-3', 'D', '取消市场机制', 40),
    ('fund-subject-1-1', 'A', '诚实信用', 10),
    ('fund-subject-1-1', 'B', '勤勉尽责', 20),
    ('fund-subject-1-1', 'C', '风险隔离', 30),
    ('fund-subject-1-1', 'D', '随意承诺收益', 40),
    ('fund-subject-1-2', 'A', '了解投资者风险承受能力', 10),
    ('fund-subject-1-2', 'B', '向所有客户统一推荐高风险产品', 20),
    ('fund-subject-1-2', 'C', '匹配产品风险等级', 30),
    ('fund-subject-1-2', 'D', '充分揭示风险', 40),
    ('fund-subject-1-3', 'A', '保证基金收益', 10),
    ('fund-subject-1-3', 'B', '保管基金财产', 20),
    ('fund-subject-1-3', 'C', '代替投资者决策', 30),
    ('fund-subject-1-3', 'D', '发布市场传闻', 40),
    ('teacher-comprehensive-1', 'A', '爱岗敬业', 10),
    ('teacher-comprehensive-1', 'B', '关爱学生', 20),
    ('teacher-comprehensive-1', 'C', '教书育人', 30),
    ('teacher-comprehensive-1', 'D', '终身学习', 40),
    ('teacher-comprehensive-2', 'A', '尊重学生人格', 10),
    ('teacher-comprehensive-2', 'B', '体罚学生', 20),
    ('teacher-comprehensive-2', 'C', '公平对待学生', 30),
    ('teacher-comprehensive-2', 'D', '主动进行专业提升', 40),
    ('teacher-comprehensive-3', 'A', '立即公开羞辱学生', 10),
    ('teacher-comprehensive-3', 'B', '先稳定课堂并了解情况', 20),
    ('teacher-comprehensive-3', 'C', '直接上报并停止教学', 30),
    ('teacher-comprehensive-3', 'D', '完全置之不理', 40)
ON DUPLICATE KEY UPDATE
    content = VALUES(content),
    sort_order = VALUES(sort_order);

INSERT INTO question_tag (question_id, tag_name, sort_order)
VALUES
    ('english-core-1', '高频词汇', 10),
    ('english-core-1', '易错辨析', 20),
    ('english-core-2', '高频词汇', 10),
    ('english-core-2', '词性判断', 20),
    ('english-core-3', '高频词汇', 10),
    ('english-core-3', '基础记忆', 20),
    ('english-reading-1', '阅读技巧', 10),
    ('english-reading-1', '易错辨析', 20),
    ('english-reading-2', '阅读技巧', 10),
    ('english-reading-2', '定位能力', 20),
    ('english-reading-3', '阅读技巧', 10),
    ('english-reading-3', '作者态度', 20),
    ('politics-basic-1', '基础概念', 10),
    ('politics-basic-1', '高频选择题', 20),
    ('politics-basic-2', '基础概念', 10),
    ('politics-basic-2', '实践原理', 20),
    ('politics-basic-3', '基础概念', 10),
    ('politics-basic-3', '真理观', 20),
    ('politics-advanced-1', '强化阶段', 10),
    ('politics-advanced-1', '高频选择题', 20),
    ('politics-advanced-2', '强化阶段', 10),
    ('politics-advanced-2', '时政理论', 20),
    ('politics-advanced-3', '强化阶段', 10),
    ('politics-advanced-3', '共同富裕', 20),
    ('fund-subject-1-1', '高频题', 10),
    ('fund-subject-1-1', '核心知识点', 20),
    ('fund-subject-1-2', '高频题', 10),
    ('fund-subject-1-2', '适当性管理', 20),
    ('fund-subject-1-3', '核心知识点', 10),
    ('fund-subject-1-3', '法规常识', 20),
    ('teacher-comprehensive-1', '高频题', 10),
    ('teacher-comprehensive-1', '核心知识点', 20),
    ('teacher-comprehensive-2', '高频题', 10),
    ('teacher-comprehensive-2', '职业规范', 20),
    ('teacher-comprehensive-3', '核心知识点', 10),
    ('teacher-comprehensive-3', '情境判断', 20)
ON DUPLICATE KEY UPDATE
    sort_order = VALUES(sort_order);

INSERT INTO question_answer (question_id, answer_key, sort_order)
VALUES
    ('english-core-1', 'B', 10),
    ('english-core-2', 'A', 10),
    ('english-core-2', 'B', 20),
    ('english-core-2', 'C', 30),
    ('english-core-3', 'A', 10),
    ('english-reading-1', 'B', 10),
    ('english-reading-2', 'A', 10),
    ('english-reading-2', 'B', 20),
    ('english-reading-2', 'D', 30),
    ('english-reading-3', 'A', 10),
    ('politics-basic-1', 'B', 10),
    ('politics-basic-2', 'A', 10),
    ('politics-basic-2', 'C', 20),
    ('politics-basic-2', 'D', 30),
    ('politics-basic-3', 'B', 10),
    ('politics-advanced-1', 'B', 10),
    ('politics-advanced-2', 'A', 10),
    ('politics-advanced-2', 'B', 20),
    ('politics-advanced-2', 'C', 30),
    ('politics-advanced-3', 'B', 10),
    ('fund-subject-1-1', 'D', 10),
    ('fund-subject-1-2', 'A', 10),
    ('fund-subject-1-2', 'C', 20),
    ('fund-subject-1-2', 'D', 30),
    ('fund-subject-1-3', 'B', 10),
    ('teacher-comprehensive-1', 'B', 10),
    ('teacher-comprehensive-2', 'A', 10),
    ('teacher-comprehensive-2', 'C', 20),
    ('teacher-comprehensive-2', 'D', 30),
    ('teacher-comprehensive-3', 'B', 10)
ON DUPLICATE KEY UPDATE
    sort_order = VALUES(sort_order);

INSERT INTO question_wrong_stat (student_id, question_id, answered_count, wrong_count, last_answered_at, last_wrong_at, version)
VALUES
    (1001, 'politics-basic-1', 9, 5, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'english-core-1', 8, 4, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'teacher-comprehensive-2', 7, 4, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'fund-subject-1-2', 6, 3, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'politics-advanced-2', 5, 3, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'english-core-2', 5, 2, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'politics-basic-2', 4, 2, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'fund-subject-1-1', 3, 1, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'politics-advanced-1', 3, 1, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0),
    (1001, 'teacher-comprehensive-1', 3, 1, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), 0)
ON DUPLICATE KEY UPDATE
    answered_count = VALUES(answered_count),
    wrong_count = VALUES(wrong_count),
    last_answered_at = VALUES(last_answered_at),
    last_wrong_at = VALUES(last_wrong_at),
    version = VALUES(version);

DELETE FROM student_dashboard_block WHERE template_id = 1;
DELETE FROM student_dashboard_template WHERE id = 1;

INSERT INTO student_dashboard_template (
    id,
    template_code,
    title,
    template_name,
    current_question_bank,
    current_recite_plan,
    active
)
VALUES
    (1, 'default-dashboard', '我的刷题总结', '学习复盘模板', '考研英语 · 核心词汇', '考研核心词汇 · Day 2', 1);

INSERT INTO student_dashboard_block (template_id, block_key, label, content, sort_order)
VALUES
    (1, 'rhythm', '当前节奏', '当前以考研英语核心词汇和考研政治基础阶段为常刷主线，先稳定做 20 题一组，再逐步扩到 40 题。', 10),
    (1, 'focus', '近期重点', '最近错题集中在阅读态度判断、政治基础概念和教师职业规范，多刷高频错题后再补细分标签。', 20),
    (1, 'next', '下一步安排', '继续保持每日刷题和背诵计划同步推进，先完成 Day 2，再回看 Top 错题榜前几题的解析。', 30)
ON DUPLICATE KEY UPDATE
    label = VALUES(label),
    content = VALUES(content),
    sort_order = VALUES(sort_order);

INSERT INTO vocabulary_book (id, name, description, active, sort_order)
VALUES
    ('kaoyan-core-vocab', '考研核心词汇', '基础高频单词，适合背诵计划联调', 1, 10),
    ('cert-fund-vocab', '基金从业高频词', '金融类资格考试常见高频词', 1, 20)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    active = VALUES(active),
    sort_order = VALUES(sort_order);

INSERT INTO vocabulary_word (id, book_id, english, chinese, part_of_speech, sort_order, status)
VALUES
    (1, 'kaoyan-core-vocab', 'abandon', '放弃', 'v.', 10, 1),
    (2, 'kaoyan-core-vocab', 'maintain', '维持', 'v.', 20, 1),
    (3, 'kaoyan-core-vocab', 'issue', '问题', 'n.', 30, 1),
    (4, 'kaoyan-core-vocab', 'require', '需要', 'v.', 40, 1),
    (5, 'kaoyan-core-vocab', 'decline', '下降', 'v.', 50, 1),
    (6, 'kaoyan-core-vocab', 'enhance', '提高', 'v.', 60, 1),
    (7, 'kaoyan-core-vocab', 'accurate', '准确的', 'adj.', 70, 1),
    (8, 'kaoyan-core-vocab', 'approach', '方法', 'n.', 80, 1),
    (9, 'kaoyan-core-vocab', 'consume', '消耗', 'v.', 90, 1),
    (10, 'kaoyan-core-vocab', 'constant', '持续的', 'adj.', 100, 1),
    (11, 'kaoyan-core-vocab', 'distinguish', '区分', 'v.', 110, 1),
    (12, 'kaoyan-core-vocab', 'significant', '重要的', 'adj.', 120, 1),
    (13, 'kaoyan-core-vocab', 'acquire', '获得', 'v.', 130, 1),
    (14, 'kaoyan-core-vocab', 'adapt', '适应', 'v.', 140, 1),
    (15, 'kaoyan-core-vocab', 'evaluate', '评估', 'v.', 150, 1),
    (16, 'kaoyan-core-vocab', 'illustrate', '阐明', 'v.', 160, 1),
    (17, 'kaoyan-core-vocab', 'justify', '证明合理', 'v.', 170, 1),
    (18, 'kaoyan-core-vocab', 'retain', '保留', 'v.', 180, 1),
    (101, 'cert-fund-vocab', 'compliance', '合规', 'n.', 10, 1),
    (102, 'cert-fund-vocab', 'custodian', '托管人', 'n.', 20, 1),
    (103, 'cert-fund-vocab', 'liquidity', '流动性', 'n.', 30, 1),
    (104, 'cert-fund-vocab', 'allocation', '配置', 'n.', 40, 1),
    (105, 'cert-fund-vocab', 'redemption', '赎回', 'n.', 50, 1),
    (106, 'cert-fund-vocab', 'subscription', '认购', 'n.', 60, 1),
    (107, 'cert-fund-vocab', 'disclosure', '披露', 'n.', 70, 1),
    (108, 'cert-fund-vocab', 'benchmark', '基准', 'n.', 80, 1),
    (109, 'cert-fund-vocab', 'prospectus', '招募说明书', 'n.', 90, 1),
    (110, 'cert-fund-vocab', 'trustee', '受托人', 'n.', 100, 1),
    (111, 'cert-fund-vocab', 'settlement', '结算', 'n.', 110, 1),
    (112, 'cert-fund-vocab', 'suitability', '适当性', 'n.', 120, 1)
ON DUPLICATE KEY UPDATE
    book_id = VALUES(book_id),
    english = VALUES(english),
    chinese = VALUES(chinese),
    part_of_speech = VALUES(part_of_speech),
    sort_order = VALUES(sort_order),
    status = VALUES(status);

INSERT INTO recite_plan (
    id,
    student_id,
    book_id,
    book_name,
    daily_count,
    total_words,
    total_days,
    status,
    superseded_at
)
VALUES
    (1, 1001, 'kaoyan-core-vocab', '考研核心词汇', 6, 18, 3, 'ACTIVE', NULL),
    (2, 1001, 'cert-fund-vocab', '基金从业高频词', 4, 12, 3, 'SUPERSEDED', CURRENT_TIMESTAMP(3))
ON DUPLICATE KEY UPDATE
    student_id = VALUES(student_id),
    book_id = VALUES(book_id),
    book_name = VALUES(book_name),
    daily_count = VALUES(daily_count),
    total_words = VALUES(total_words),
    total_days = VALUES(total_days),
    status = VALUES(status),
    superseded_at = VALUES(superseded_at);

INSERT INTO recite_plan_day (
    id,
    plan_id,
    day_number,
    day_label,
    start_word_order,
    end_word_order,
    total_count,
    status,
    study_completed_at,
    last_accuracy,
    last_correct_count,
    last_wrong_count,
    completed_at
)
VALUES
    (1, 1, 1, 'Day 1', 10, 60, 6, 'COMPLETED', CURRENT_TIMESTAMP(3), '83%', 5, 1, CURRENT_TIMESTAMP(3)),
    (2, 1, 2, 'Day 2', 70, 120, 6, 'PENDING', NULL, NULL, NULL, NULL, NULL),
    (3, 1, 3, 'Day 3', 130, 180, 6, 'PENDING', NULL, NULL, NULL, NULL, NULL),
    (11, 2, 1, 'Day 1', 10, 40, 4, 'COMPLETED', CURRENT_TIMESTAMP(3), '100%', 4, 0, CURRENT_TIMESTAMP(3)),
    (12, 2, 2, 'Day 2', 50, 80, 4, 'PENDING', NULL, NULL, NULL, NULL, NULL),
    (13, 2, 3, 'Day 3', 90, 120, 4, 'PENDING', NULL, NULL, NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE
    plan_id = VALUES(plan_id),
    day_number = VALUES(day_number),
    day_label = VALUES(day_label),
    start_word_order = VALUES(start_word_order),
    end_word_order = VALUES(end_word_order),
    total_count = VALUES(total_count),
    status = VALUES(status),
    study_completed_at = VALUES(study_completed_at),
    last_accuracy = VALUES(last_accuracy),
    last_correct_count = VALUES(last_correct_count),
    last_wrong_count = VALUES(last_wrong_count),
    completed_at = VALUES(completed_at);

INSERT INTO recite_day_record (
    id,
    plan_day_id,
    plan_id,
    student_id,
    book_name,
    day_label,
    mode,
    total_count,
    correct_count,
    wrong_count,
    accuracy,
    answers_json
)
VALUES
    (
        1,
        1,
        1,
        1001,
        '考研核心词汇',
        'Day 1',
        'cn_to_en',
        6,
        5,
        1,
        '83%',
        JSON_ARRAY(
            JSON_OBJECT('wordId', 1, 'english', 'abandon', 'chinese', '放弃', 'partOfSpeech', 'v.', 'prompt', '放弃', 'userAnswer', 'abandon', 'standardAnswer', 'abandon', 'correct', TRUE),
            JSON_OBJECT('wordId', 2, 'english', 'maintain', 'chinese', '维持', 'partOfSpeech', 'v.', 'prompt', '维持', 'userAnswer', 'maintain', 'standardAnswer', 'maintain', 'correct', TRUE),
            JSON_OBJECT('wordId', 3, 'english', 'issue', 'chinese', '问题', 'partOfSpeech', 'n.', 'prompt', '问题', 'userAnswer', 'issue', 'standardAnswer', 'issue', 'correct', TRUE),
            JSON_OBJECT('wordId', 4, 'english', 'require', 'chinese', '需要', 'partOfSpeech', 'v.', 'prompt', '需要', 'userAnswer', 'require', 'standardAnswer', 'require', 'correct', TRUE),
            JSON_OBJECT('wordId', 5, 'english', 'decline', 'chinese', '下降', 'partOfSpeech', 'v.', 'prompt', '下降', 'userAnswer', 'decline', 'standardAnswer', 'decline', 'correct', TRUE),
            JSON_OBJECT('wordId', 6, 'english', 'enhance', 'chinese', '提高', 'partOfSpeech', 'v.', 'prompt', '提高', 'userAnswer', 'improve', 'standardAnswer', 'enhance', 'correct', FALSE)
        )
    ),
    (
        11,
        11,
        2,
        1001,
        '基金从业高频词',
        'Day 1',
        'en_to_cn',
        4,
        4,
        0,
        '100%',
        JSON_ARRAY(
            JSON_OBJECT('wordId', 101, 'english', 'compliance', 'chinese', '合规', 'partOfSpeech', 'n.', 'prompt', 'compliance', 'userAnswer', '合规', 'standardAnswer', '合规', 'correct', TRUE),
            JSON_OBJECT('wordId', 102, 'english', 'custodian', 'chinese', '托管人', 'partOfSpeech', 'n.', 'prompt', 'custodian', 'userAnswer', '托管人', 'standardAnswer', '托管人', 'correct', TRUE),
            JSON_OBJECT('wordId', 103, 'english', 'liquidity', 'chinese', '流动性', 'partOfSpeech', 'n.', 'prompt', 'liquidity', 'userAnswer', '流动性', 'standardAnswer', '流动性', 'correct', TRUE),
            JSON_OBJECT('wordId', 104, 'english', 'allocation', 'chinese', '配置', 'partOfSpeech', 'n.', 'prompt', 'allocation', 'userAnswer', '配置', 'standardAnswer', '配置', 'correct', TRUE)
        )
    )
ON DUPLICATE KEY UPDATE
    plan_day_id = VALUES(plan_day_id),
    plan_id = VALUES(plan_id),
    student_id = VALUES(student_id),
    book_name = VALUES(book_name),
    day_label = VALUES(day_label),
    mode = VALUES(mode),
    total_count = VALUES(total_count),
    correct_count = VALUES(correct_count),
    wrong_count = VALUES(wrong_count),
    accuracy = VALUES(accuracy),
    answers_json = VALUES(answers_json);

-- practice_session / practice_session_question / practice_answer_record
-- 在统一测试前通过 sql/reset-student-test-baseline.sql 清空运行态，再重跑当前 seed。
