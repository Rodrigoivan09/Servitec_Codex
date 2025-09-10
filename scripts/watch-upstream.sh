#!/usr/bin/env bash

set -euo pipefail

# Watch a Git upstream branch for new commits and optionally auto-pull.
#
# Usage:
#   bash scripts/watch-upstream.sh [--interval SECONDS] [--auto-pull] [--branch <remote/branch>]
#
# Defaults:
#   --interval 60            Poll every 60 seconds
#   --branch <upstream>      Uses current branch's upstream if set; else origin/<current|main|master>
#   --auto-pull              If set, runs `git pull --rebase --autostash` when changes detected

interval=60
auto_pull=false
branch=""

log() {
  printf "[%s] %s\n" "$(date '+%Y-%m-%d %H:%M:%S')" "$*"
}

usage() {
  sed -n '1,20p' "$0"
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --interval)
      interval=${2:-}
      if [[ -z "$interval" || ! "$interval" =~ ^[0-9]+$ ]]; then
        echo "Invalid --interval value" >&2; exit 2
      fi
      shift 2
      ;;
    --auto-pull)
      auto_pull=true
      shift
      ;;
    --branch)
      branch=${2:-}
      if [[ -z "$branch" ]]; then
        echo "--branch requires a value like origin/main" >&2; exit 2
      fi
      shift 2
      ;;
    -h|--help)
      usage; exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage
      exit 2
      ;;
  esac
done

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "This script must be run inside a Git repository" >&2
  exit 1
fi

# Determine default upstream branch if not provided
if [[ -z "$branch" ]]; then
  if upstream=$(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null); then
    branch="$upstream"
  else
    current_branch=$(git symbolic-ref --quiet --short HEAD || echo "")
    if [[ -n "$current_branch" ]] && git show-ref --verify --quiet "refs/remotes/origin/$current_branch"; then
      branch="origin/$current_branch"
    elif git show-ref --verify --quiet "refs/remotes/origin/main"; then
      branch="origin/main"
    elif git show-ref --verify --quiet "refs/remotes/origin/master"; then
      branch="origin/master"
    else
      echo "Could not determine a remote tracking branch. Specify with --branch origin/<name>." >&2
      exit 1
    fi
  fi
fi

log "Watching upstream: $branch (interval: ${interval}s, auto-pull: ${auto_pull})"
trap 'echo; log "Stopping watcher"; exit 0' INT TERM

while true; do
  # Fetch silently; failures should be visible
  if ! git fetch -q --prune --tags; then
    log "git fetch failed; will retry"
    sleep "$interval"
    continue
  fi

  local_head=$(git rev-parse HEAD)
  if ! remote_head=$(git rev-parse "$branch" 2>/dev/null); then
    log "Upstream $branch not found after fetch; will retry"
    sleep "$interval"
    continue
  fi

  if [[ "$local_head" != "$remote_head" ]]; then
    ahead=$(git rev-list --left-right --count "$local_head...$remote_head" | awk '{print $1 ":" $2}')
    IFS=":" read -r ahead_count behind_count <<< "$ahead"
    if [[ ${behind_count:-0} -gt 0 ]]; then
      log "New commits available upstream ($branch). Behind by $behind_count."
      git --no-pager log --oneline --decorate --no-color "${local_head}..${branch}" | sed 's/^/  /'
      if [[ "$auto_pull" == true ]]; then
        log "Auto-pulling with rebase and autostash..."
        if git pull --rebase --autostash --ff-only --no-edit --no-stat >/dev/null 2>&1; then
          log "Pull complete. Now at $(git rev-parse --short HEAD)."
        else
          # Fallback without --ff-only to show errors clearly
          if git pull --rebase --autostash; then
            log "Pull complete after rebase."
          else
            log "Auto-pull encountered conflicts. Resolve manually and restart watcher."
            exit 1
          fi
        fi
      else
        log "Run: git pull --rebase --autostash"
      fi
    else
      # local ahead or diverged; still notify
      log "Local branch is ahead or diverged from $branch."
    fi
  fi

  sleep "$interval"
done

