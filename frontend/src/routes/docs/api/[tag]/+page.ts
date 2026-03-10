import { error } from '@sveltejs/kit';
import { getTagBySlug, getOperationsByTagSlug } from '$lib/docs/openapi';
import type { PageLoad } from './$types';

export const load: PageLoad = ({ params }) => {
  const tagSlug = params.tag;
  const tag = getTagBySlug(tagSlug);

  if (!tag) {
    throw error(404, {
      message: `API documentation for tag "${tagSlug}" not found.`
    });
  }

  const operations = getOperationsByTagSlug(tagSlug);

  return {
    tag,
    operations
  };
};
