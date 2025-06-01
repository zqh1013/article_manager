需要手动创建全文索引
ALTER TABLE articles 
ADD FULLTEXT INDEX idx_content (content) 
WITH PARSER ngram;