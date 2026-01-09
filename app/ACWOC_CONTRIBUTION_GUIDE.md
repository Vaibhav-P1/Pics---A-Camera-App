# ACWOC Contribution Guide 🚀

Welcome to the repository! We're excited to have you contribute as part of **ACWOC**. This guide will walk you through **forking the repository, setting it up locally, and raising a Pull Request (PR)** — even if this is your first open-source contribution.

---

## 🍴 Step 1: Fork the Repository

1. Open this repository on GitHub.
2. Click the **Fork** button (top-right).
3. This creates a copy of the repository under your GitHub account.

✅ You will now work on your fork, not the original repository.

---

## 💻 Step 2: Clone the Fork to Your Local Machine

1. Go to your forked repository
2. Click **Code → HTTPS**
3. Copy the repository URL

Run the following command in your terminal:

```bash
git clone <your-forked-repo-url>
```

Example:

```bash
git clone https://github.com/your-username/project-name.git
```

Move into the project directory:

```bash
cd project-name
```

---

## 🔗 Step 3: Add Upstream Remote (IMPORTANT)

Adding an upstream remote allows you to sync your fork with the original repository.

```bash
git remote add upstream https://github.com/original-owner/project-name.git
```

Verify the remotes:

```bash
git remote -v
```

You should see:
- **origin** → your forked repository
- **upstream** → original repository

---

## ⚙️ Step 4: Project Setup (Local Installation)

Follow setup instructions based on the project's tech stack.

### Example (General Setup)

```bash
# Install dependencies

📌 Always refer to **README.md** for project-specific setup instructions.

---

## 🌱 Step 5: Create a New Branch

⚠️ **Never make changes directly on the main branch.**

Create a new branch for your work:

```bash
git checkout -b feature/your-feature-name
```

Example:

```bash
git checkout -b fix-login-bug
```

---

## 🧑‍💻 Step 6: Make Your Changes

- Follow the existing code style
- Keep changes relevant to the issue
- Test your changes locally before committing

---

## 💾 Step 7: Commit Your Changes

Stage your changes:

```bash
git add .
```

Commit with a meaningful message:

```bash
git commit -m "Add: meaningful commit message"
```

Example:

```bash
git commit -m "Fix: crash on empty input"
```

---

## ⬆️ Step 8: Push Changes to Your Fork

Push your branch to your forked repository:

```bash
git push origin feature/your-feature-name
```

---

## 🔁 Step 9: Raise a Pull Request (PR)

1. Go to your fork on GitHub
2. Click **Compare & Pull Request**
3. Add a clear title and description
4. Submit the Pull Request

🎉 **Congratulations! Your PR has been raised.**

---

## 🔄 Step 10: Keep Your Fork Updated

Before starting new work, sync your fork with the upstream repository:

```bash
git checkout main
git pull upstream main
git push origin main
```

---

## 🧠 Contribution Guidelines

- One Pull Request per feature or bug fix
- Write clean and readable code
- Follow commit message conventions
- Do not spam PRs
- Respect maintainers and reviewers

---

## ❓ Need Help?

If you're new to Git or stuck with:
- Merge conflicts
- PR reviews
- Git commands

Feel free to open an issue or ask in the discussions section. We're happy to help 🙂

---

**Happy contributing 💙 Let's build something awesome together!**