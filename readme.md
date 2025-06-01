[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/zqh1013/article_manager)
需要手动创建全文索引
ALTER TABLE articles 
ADD FULLTEXT INDEX idx_content (content) 
WITH PARSER ngram;