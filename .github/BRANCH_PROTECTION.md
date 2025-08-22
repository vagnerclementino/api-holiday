# 🛡️ Branch Protection Configuration

This document describes the required branch protection rules for the repository to ensure the Quality workflow is enforced.

## 🎯 Required Branch Protection Rules

### Main Branch Protection

Configure the following settings for the `main` branch in GitHub:

#### ✅ **Require Status Checks**
- ☑️ Require status checks to pass before merging
- ☑️ Require branches to be up to date before merging

#### 📋 **Required Status Checks**
Add these exact check names (case-sensitive):
- `🏗️ Build Application`
- `🎨 Code Style Check`  
- `🧪 Unit Tests`
- `🔗 Integration Tests`
- `🚪 Quality Gate`

#### 🔒 **Additional Protection Rules**
- ☑️ Require pull request reviews before merging
- ☑️ Dismiss stale PR approvals when new commits are pushed
- ☑️ Require review from code owners (if CODEOWNERS file exists)
- ☑️ Restrict pushes that create files larger than 100MB
- ☑️ Require linear history (optional, recommended)

#### 👥 **Who Can Merge**
- ☑️ Include administrators in these restrictions
- ☑️ Allow force pushes: **Disabled**
- ☑️ Allow deletions: **Disabled**

## 🚀 GitHub Settings Configuration

### Step-by-Step Setup

1. **Navigate to Repository Settings**
   ```
   Repository → Settings → Branches
   ```

2. **Add Branch Protection Rule**
   ```
   Branch name pattern: main
   ```

3. **Configure Protection Settings**
   ```
   ✅ Require a pull request before merging
   ✅ Require status checks to pass before merging
   ✅ Require branches to be up to date before merging
   ```

4. **Add Required Status Checks**
   ```
   Search for and add each job name:
   - 🏗️ Build Application
   - 🎨 Code Style Check
   - 🧪 Unit Tests  
   - 🔗 Integration Tests
   - 🚪 Quality Gate
   ```

5. **Additional Restrictions**
   ```
   ✅ Restrict pushes that create files larger than 100MB
   ✅ Include administrators
   ```

## 🔧 Alternative: GitHub CLI Configuration

You can also configure branch protection using GitHub CLI:

```bash
# Install GitHub CLI if not already installed
# https://cli.github.com/

# Configure branch protection
gh api repos/:owner/:repo/branches/main/protection \
  --method PUT \
  --field required_status_checks='{"strict":true,"contexts":["🏗️ Build Application","🎨 Code Style Check","🧪 Unit Tests","🔗 Integration Tests","🚪 Quality Gate"]}' \
  --field enforce_admins=true \
  --field required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":true}' \
  --field restrictions=null
```

## 📋 Verification

After configuring branch protection, verify the setup:

1. **Create a test PR** with intentional issues
2. **Verify workflow runs** on PR creation
3. **Confirm merge is blocked** if any check fails
4. **Test successful merge** when all checks pass

## 🚨 Troubleshooting

### Common Issues

#### Status Checks Not Found
- Ensure the workflow has run at least once
- Check that job names match exactly (including emojis)
- Verify the workflow file is in `.github/workflows/`

#### Workflow Not Triggering
- Check the `on:` triggers in the workflow file
- Ensure PR is targeting the correct branch (`main`)
- Verify the workflow file syntax is valid

#### Permission Issues
- Ensure repository has Actions enabled
- Check that the user has appropriate permissions
- Verify GitHub token permissions if using external actions

## 📚 Additional Resources

- [GitHub Branch Protection Documentation](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/about-protected-branches)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Status Checks Documentation](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/defining-the-mergeability-of-pull-requests/about-status-checks)

---

**Note**: These settings ensure that no code can be merged to `main` without passing all quality checks defined in the Quality workflow.
