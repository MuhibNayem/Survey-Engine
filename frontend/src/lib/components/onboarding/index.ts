import Root from './OnboardingTour.svelte';

export {
	Root,
	//
	Root as OnboardingTour
};

export interface OnboardingStep {
	title: string;
	description: string;
	targetSelector: string;
}

export interface OnboardingTourProps {
	steps: OnboardingStep[];
	onComplete?: () => void;
}
