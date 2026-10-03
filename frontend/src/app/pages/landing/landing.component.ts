import { AfterViewInit, Component, OnDestroy, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';

gsap.registerPlugin(ScrollTrigger);

@Component({
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss'
})
export class LandingComponent implements AfterViewInit, OnDestroy {
  readonly openStory = signal(0);
  readonly activeQuote = signal(0);
  private animationContext?: gsap.Context;

  readonly stories = [
    { title: 'Find your pace', copy: 'Log a run, a long walk, or a few quiet minutes on the mat.', image: 'https://picsum.photos/seed/fittracker-motion/1200/900' },
    { title: 'See the pattern', copy: 'Small, steady sessions add up. Your week makes the progress visible.', image: 'https://picsum.photos/seed/fittracker-trail/1200/900' },
    { title: 'Make it yours', copy: 'Build a routine around the life you actually have.', image: 'https://picsum.photos/seed/fittracker-studio/1200/900' }
  ];
  readonly quotes = [
    { quote: 'I do not need a perfect week. I can notice the days I showed up.', name: 'A note to yourself', detail: 'For the next time you need it', image: 'https://picsum.photos/seed/fittracker-maya/180/180' },
    { quote: 'Consistency can be quieter than I expected. It still counts.', name: 'A note to yourself', detail: 'For the days that feel small', image: 'https://picsum.photos/seed/fittracker-arjun/180/180' },
    { quote: 'This is my practice. It gets to look like my life.', name: 'A note to yourself', detail: 'For the path you are making', image: 'https://picsum.photos/seed/fittracker-nina/180/180' }
  ];

  ngAfterViewInit(): void {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;
    this.animationContext = gsap.context(() => {
      gsap.from('.hero-copy > *', { y: 28, opacity: 0, duration: 0.8, stagger: 0.12, ease: 'power3.out' });
      gsap.to('.reveal-word', {
        opacity: 1, stagger: 0.06, ease: 'none',
        scrollTrigger: { trigger: '.manifesto', start: 'top 75%', end: 'bottom 45%', scrub: 1 }
      });
      if (window.innerWidth > 780) {
        gsap.fromTo('.story-card', { y: 100, opacity: 0.55, rotate: 1.5 }, {
          y: 0, opacity: 1, rotate: 0, stagger: -0.12, ease: 'none',
          scrollTrigger: { trigger: '.story-section', start: 'top top', end: '+=70%', scrub: 1, pin: '.story-pin' }
        });
      } else {
        gsap.from('.story-card', { y: 24, opacity: 0, stagger: 0.12, duration: 0.65, scrollTrigger: { trigger: '.story-section', start: 'top 80%' } });
      }
      gsap.utils.toArray<HTMLElement>('.feature-card').forEach(card => {
        gsap.fromTo(card, { scale: 0.94, opacity: 0.55 }, {
          scale: 1, opacity: 1, duration: 1,
          scrollTrigger: { trigger: card, start: 'top 88%', end: 'top 45%', scrub: 1 }
        });
      });
    });
  }

  ngOnDestroy(): void { this.animationContext?.revert(); }

  changeQuote(direction: number): void {
    this.activeQuote.update(index => (index + direction + this.quotes.length) % this.quotes.length);
  }
}
