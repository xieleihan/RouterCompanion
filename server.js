const express = require('express');
const cors = require('cors');
const { Octokit } = require('@octokit/rest');

const app = express();
const PORT = process.env.PORT || 3000;

// 中间件
app.use(cors());
app.use(express.json());
app.use(express.static('.'));

// GitHub 配置
const GITHUB_OWNER = process.env.GITHUB_OWNER || 'xieleihan';
const GITHUB_REPO = process.env.GITHUB_REPO || 'RouterCompanion';

// 构建APK的API端点
app.post('/api/build', async (req, res) => {
    try {
        const { domain, token } = req.body;

        if (!domain || !token) {
            return res.status(400).json({
                success: false,
                error: 'Missing domain or token'
            });
        }

        // 验证域名格式
        try {
            new URL(domain);
        } catch (e) {
            return res.status(400).json({
                success: false,
                error: 'Invalid domain format'
            });
        }

        // 初始化 Octokit
        const octokit = new Octokit({
            auth: token,
        });

        // 触发 repository dispatch 事件
        await octokit.rest.repos.createDispatchEvent({
            owner: GITHUB_OWNER,
            repo: GITHUB_REPO,
            event_type: 'build-with-domain',
            client_payload: {
                domain: domain,
                timestamp: new Date().toISOString(),
                source: 'api'
            }
        });

        res.json({
            success: true,
            message: 'Build triggered successfully',
            data: {
                domain: domain,
                timestamp: new Date().toISOString(),
                links: {
                    actions: `https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/actions`,
                    releases: `https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/releases`
                }
            }
        });

    } catch (error) {
        console.error('Build trigger error:', error);
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
});

// 获取构建状态的API端点
app.get('/api/status', async (req, res) => {
    try {
        const { token } = req.query;

        if (!token) {
            return res.status(400).json({
                success: false,
                error: 'Missing token'
            });
        }

        const octokit = new Octokit({
            auth: token,
        });

        // 获取最新的工作流运行状态
        const { data: runs } = await octokit.rest.actions.listWorkflowRunsForRepo({
            owner: GITHUB_OWNER,
            repo: GITHUB_REPO,
            per_page: 10
        });

        // 获取最新的发布
        const { data: releases } = await octokit.rest.repos.listReleases({
            owner: GITHUB_OWNER,
            repo: GITHUB_REPO,
            per_page: 5
        });

        res.json({
            success: true,
            data: {
                recent_runs: runs.workflow_runs.map(run => ({
                    id: run.id,
                    name: run.name,
                    status: run.status,
                    conclusion: run.conclusion,
                    created_at: run.created_at,
                    updated_at: run.updated_at,
                    html_url: run.html_url
                })),
                recent_releases: releases.map(release => ({
                    id: release.id,
                    name: release.name,
                    tag_name: release.tag_name,
                    published_at: release.published_at,
                    html_url: release.html_url,
                    assets: release.assets.map(asset => ({
                        name: asset.name,
                        download_count: asset.download_count,
                        browser_download_url: asset.browser_download_url
                    }))
                }))
            }
        });

    } catch (error) {
        console.error('Status check error:', error);
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
});

// 健康检查端点
app.get('/health', (req, res) => {
    res.json({
        status: 'OK',
        timestamp: new Date().toISOString(),
        service: 'RouterCompanion Build API'
    });
});

// 服务主页
app.get('/', (req, res) => {
    res.send(`
<!DOCTYPE html>
<html>
<head>
    <title>RouterCompanion Build API</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h1 { color: #333; }
        .endpoint { background: #f5f5f5; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .method { background: #4CAF50; color: white; padding: 3px 8px; border-radius: 3px; font-size: 12px; }
        code { background: #eee; padding: 2px 4px; border-radius: 3px; }
    </style>
</head>
<body>
    <h1>🚀 RouterCompanion Build API</h1>
    <p>这是 RouterCompanion APK 自动构建服务的 API 接口。</p>
    
    <div class="endpoint">
        <h3><span class="method">POST</span> /api/build</h3>
        <p>触发APK构建</p>
        <p><strong>参数:</strong> <code>domain</code>, <code>token</code></p>
    </div>
    
    <div class="endpoint">
        <h3><span class="method">GET</span> /api/status</h3>
        <p>获取构建状态</p>
        <p><strong>参数:</strong> <code>token</code></p>
    </div>
    
    <div class="endpoint">
        <h3><span class="method">GET</span> /health</h3>
        <p>服务健康检查</p>
    </div>
    
    <p><a href="/build-interface.html">📱 Web构建界面</a> | <a href="/api-docs.html">📖 API文档</a></p>
</body>
</html>
    `);
});

// 启动服务器
app.listen(PORT, () => {
    console.log(`🚀 RouterCompanion Build API Server running on port ${PORT}`);
    console.log(`📱 Web Interface: http://localhost:${PORT}/build-interface.html`);
    console.log(`📖 API Docs: http://localhost:${PORT}/api-docs.html`);
});

module.exports = app;